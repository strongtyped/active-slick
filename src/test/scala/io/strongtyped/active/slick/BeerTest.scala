package io.strongtyped.active.slick

import io.strongtyped.active.slick.models.{Beer, Supplier}
import io.strongtyped.active.slick.components.Components.instance._
import org.scalatest.{OptionValues, Matchers, FunSuite}

class BeerTest extends FunSuite with Matchers with OptionValues {
  test("A Beer should be persistable") {

    DB { implicit sess =>
      val supplier = Supplier("Acme, Inc.").save

      supplier.id shouldBe defined

      supplier.id.map { supId =>
        val coffee = Beer("Abc", supId, 3.2).save
        coffee.supplier.value shouldBe supplier
      }
    }
  }
}
