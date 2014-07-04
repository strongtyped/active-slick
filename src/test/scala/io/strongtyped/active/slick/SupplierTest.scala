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
import io.strongtyped.active.slick.models.Supplier
import org.scalatest.{OptionValues, Matchers, FunSuite}

class SupplierTest extends FunSuite  with Matchers with OptionValues {

  test("A Supplier should be persistable") {

    DB { implicit sess =>
      val initialCount = Suppliers.count

      val supplier = Supplier("Acme, Inc.")
      supplier.id should not be defined

      val persistedSupp = supplier.save
      persistedSupp.id shouldBe defined

      Suppliers.count shouldBe (initialCount + 1)

      persistedSupp.delete

      Suppliers.count shouldBe initialCount

    }
  }

  test("A Supplier is versionable") {
    DB { implicit sess =>
      val supplier = Supplier("abc")
      // not version yet
      supplier.version shouldBe 0

      val persistedSupp = supplier.save
      persistedSupp.version should not be 0

      // modify two versions and try to persist them
      val suppWithNewVersion = persistedSupp.copy(name = "abc1").save

      intercept[StaleObjectStateException[Supplier]] {
        // supplier was persisted in the mean time, so version must be different by now
        persistedSupp.copy(name = "abc2").save
      }

      // supplier with new version can be persisted again
      suppWithNewVersion.copy(name = "abc").save
    }
  }
}
