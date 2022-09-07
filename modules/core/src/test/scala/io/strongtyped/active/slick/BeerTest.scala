package io.strongtyped.active.slick

import io.strongtyped.active.slick.exceptions.RowNotFoundException
import io.strongtyped.active.slick.test.H2Suite
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class BeerTest extends AnyFlatSpec with H2Suite with Schema {

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
          supplier.id shouldBe defined
          (supplier, beer)
        }
      }
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


  override def createSchemaAction: jdbcProfile.api.DBIO[Unit] = {
    jdbcProfile.api.DBIO.seq(Suppliers.createSchema, Beers.createSchema)
  }

}
