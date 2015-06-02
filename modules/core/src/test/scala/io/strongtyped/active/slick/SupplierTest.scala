package io.strongtyped.active.slick

import java.sql.SQLException
import scala.concurrent.ExecutionContext.Implicits.global
import io.strongtyped.active.slick.exceptions.StaleObjectStateException
import io.strongtyped.active.slick.test.H2Suite
import org.scalatest._
import slick.dbio.DBIO

class SupplierTest extends FlatSpec with H2Suite with Schema {

  behavior of "A Supplier"

  it should "be persistable" in {
    val initialCount = query(Suppliers.count)

    val supplier = Supplier("Acme, Inc.")
    supplier.id should not be defined

    val savedSupplier =
      commit {
        for {
          savedSupplier <- supplier.save()
        } yield {
          savedSupplier
        }
      }
    savedSupplier.id shouldBe defined

    val countAfterSave = query(Suppliers.count)
    countAfterSave shouldBe (initialCount + 1)

    commit(savedSupplier.delete())

    val countAfterDelete = query(Suppliers.count)
    countAfterDelete shouldBe initialCount

  }

  it should "be versionable" in {

    val supplier = Supplier("abc")
    // no version yet
    supplier.version shouldBe 0

    val persistedSupp = commit(supplier.save())
    persistedSupp.version should not be 0

    // modify two versions and try to persist them
    val suppWithNewVersion = commit(persistedSupp.copy(name = "abc1").save())

    intercept[StaleObjectStateException[Supplier]] {
      // supplier was persisted in the mean time, so version must be different by now
      commit(persistedSupp.copy(name = "abc2").save())
    }

    // supplier with new version can be persisted again
    commit(suppWithNewVersion.copy(name = "abc").save())
  }

  it should "return an error when deleting a supplier with beers linked to it" in {

    val deleteResult =
      rollback {
        for {
          supplier <- Supplier("Acme, Inc.").save()
          beer <- Beer("Abc", supplier.id.get, 3.2).save()
          deleteResult <- supplier.delete().asTry
        } yield deleteResult
      }

    deleteResult.failure.exception shouldBe a[SQLException]
  }

  def createSchema: DBIO[Unit] = create

}
