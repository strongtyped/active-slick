package io.strongtyped.active.slick

import io.strongtyped.active.slick.components.Components.instance._
import io.strongtyped.active.slick.exceptions.EntityNotFoundException
import io.strongtyped.active.slick.models.{Beer, Supplier}
import org.scalatest.{FunSuite, Matchers, OptionValues, TryValues}

class BeerTest extends FunSuite with Matchers with OptionValues with TryValues {


  test("A Beer should be persistable") {
    DB.rollback { implicit sess =>
      val supplier = Supplier("Acme, Inc.").save

      supplier.id shouldBe defined

      supplier.id.map { supId =>
        val coffee = Beer("Abc", supId, 3.2).save
        coffee.supplier.value shouldBe supplier
      }
    }
  }

  test("Entity ID can't be chosen by user") {
    DB.rollback { implicit sess =>
      val supplier = Supplier("Acme, Inc.").save

      supplier.id shouldBe defined

      val supId = supplier.id.get
      val tried = Beer("Abc", supId, 3.2, Some(10)).trySave
      tried.failure.exception shouldBe a[EntityNotFoundException[_]]
    }
  }

}
