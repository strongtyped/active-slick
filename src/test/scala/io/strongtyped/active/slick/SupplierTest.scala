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

class SupplierTest extends FunSuite with DbTest  with Matchers with OptionValues {

  test("A Supplier should be persisted in DB") {

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
}
