/*
 * Copyright 2014 Renato Guerra Cavalcanti (@renatocaval)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.strongtyped.active.slick

import io.strongtyped.active.slick.components.Components.instance._
import io.strongtyped.active.slick.exceptions.StaleObjectStateException
import io.strongtyped.active.slick.models.Supplier
import io.strongtyped.active.slick.test.DbSuite
import org.scalatest._
import slick.dbio._

import scala.concurrent.ExecutionContext.Implicits.global

class SupplierTest extends DbSuite with BeforeAndAfterAll {

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

    val persistedSupp = commit(supplier.save)
    persistedSupp.version should not be 0

    // modify two versions and try to persist them
    val suppWithNewVersion = commit(persistedSupp.copy(name = "abc1").save)

    intercept[StaleObjectStateException[Supplier]] {
      // supplier was persisted in the mean time, so version must be different by now
      commit(persistedSupp.copy(name = "abc2").save)
    }

    // supplier with new version can be persisted again
    commit(suppWithNewVersion.copy(name = "abc").save)
  }

  def setupSchema: DBIO[Unit] = createSchema

  //  test("A Supplier can't be deleted if it has Beers linked to it") {

  //    DB.rollback { implicit sess =>
  //      val supplier = Supplier("Acme, Inc.").save
  //
  //      supplier.id shouldBe defined
  //
  //      supplier.id.map { supId =>
  //        val coffee = Beer("Abc", supId, 3.2).save
  //        coffee.supplier.value shouldBe supplier
  //      }
  //
  //      supplier.tryDelete.failure.exception shouldBe a[JdbcSQLException]
  //    }
  //  }

}
