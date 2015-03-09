package io.strongtyped.active.slick

import io.strongtyped.active.slick.components.Components.instance._
import io.strongtyped.active.slick.exceptions.RowNotFoundException
import io.strongtyped.active.slick.models.{Beer, Supplier}
import io.strongtyped.active.slick.test.DbSuite
import org.scalatest._
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext.Implicits.global

class BeerTest extends DbSuite {

  behavior of "A Beer"

  it should "be persistable" in {
    val (supplier, beer) =
      rollback {
        for {
          supplier <- Supplier("Acme, Inc.").save()
          beer <- Beer("Abc", supplier.id.get, 3.2).save()
          beerSupplier <- beer.supplier()
        } yield {
          beerSupplier.value shouldBe supplier
          (supplier, beer)
        }
      }
    supplier.id shouldBe defined
  }

  it should "not be persisted with an id chosen by a user" in {
    val (supplier, triedBeer) =
      rollback {
        for {
          supplier <- Supplier("Acme, Inc.").save()
          beer <- Beer("Abc", supplier.id.get, 3.2, Some(10)).save().asTry
        } yield (supplier, beer)
      }

    supplier.id shouldBe defined
    triedBeer.failure.exception shouldBe a[RowNotFoundException[_]]
  }

  def setupSchema: DBIO[Unit] = createSchema

}
