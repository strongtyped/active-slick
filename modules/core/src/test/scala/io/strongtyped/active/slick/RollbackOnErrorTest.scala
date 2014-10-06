package io.strongtyped.active.slick

import io.strongtyped.active.slick.components.Components.instance._
import io.strongtyped.active.slick.exceptions.StaleObjectStateException
import io.strongtyped.active.slick.models.Supplier
import org.scalatest.{FunSuite, Matchers, OptionValues, TryValues}

class RollbackOnErrorTest extends FunSuite with Matchers with OptionValues with TryValues {

  test("A Supplier should be persistable") {

    val supId = DB.commit { implicit sess =>
      val supplier = Supplier("abc").save
      supplier.id.get
    }


    DB.commit { implicit sess =>
      val supplier = Suppliers.findById(supId)

      // modify two versions and try to persist them
      supplier.copy(name = "abc1").save

      // this one will return a Failure,
      // but since exception is caught, no transaction rollback will be triggered
      val result = supplier.copy(name = "abc2").trySave

      result.failure.exception shouldBe a[StaleObjectStateException[_]]
    }

    DB.rollback { implicit sess =>
      val supplier = Suppliers.findById(supId)
      supplier.name shouldBe "abc"
    }
  }

}
