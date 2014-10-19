package io.strongtyped.active.slick

import io.strongtyped.active.slick.components.Components.instance._
import io.strongtyped.active.slick.exceptions.StaleObjectStateException
import io.strongtyped.active.slick.models.Supplier
import org.scalatest.{ FunSuite, Matchers, OptionValues, TryValues }

class RollbackOnErrorTest extends FunSuite with Matchers with OptionValues with TryValues {

  test("A trySave should trigger a transaction rollback") {

    val supId = DB.commit { implicit sess =>
      val supplier = Supplier("abc").save
      supplier.id.get
    }

    DB.commit { implicit sess =>
      val supplier = Suppliers.findById(supId)

      // modify two versions and try to persist them
      supplier.copy(name = "updated").save

      // this one will return a Failure due to optimistic locking
      // and transaction will be marked for rollback
      val result = supplier.copy(name = "updated-again").trySave

      result.failure.exception shouldBe a[StaleObjectStateException[_]]
    }

    DB.rollback { implicit sess =>
      val supplier = Suppliers.findById(supId)
      supplier.name shouldBe "abc"
    }
  }

  test("A trySave can be used together with autoCommit mode") {

    val supId = DB.commit { implicit sess =>
      val supplier = Supplier("abc").save
      supplier.id.get
    }

    DB.autoCommit { implicit sess =>
      val supplier = Suppliers.findById(supId)

      // modify two versions and try to persist them
      supplier.copy(name = "updated").save

      // this one will return a Failure due to optimistic locking,
      // and transaction will be marked for rollback
      val result = supplier.copy(name = "updated-again").trySave

      result.failure.exception shouldBe a[StaleObjectStateException[_]]
    }

    DB.rollback { implicit sess =>
      val supplier = Suppliers.findById(supId)
      supplier.name shouldBe "updated"
    }
  }

}
