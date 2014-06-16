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
import InMemoryDb._

object Example1 {

  def main(args:Array[String]) : Unit = {
     DB { implicit sess =>
       println("###################################")
       println(s"total suppliers = ${Suppliers.count}")
       val supplier = Supplier("Acme, Inc.")

       val persistedSupp = supplier.save
       println(s"Supplier is saved with ${persistedSupp.id}")

       println()
       println(s"total suppliers = ${Suppliers.count}")

       persistedSupp.delete
       println("Supplier deleted")
       println(s"total suppliers = ${Suppliers.count}")

       println()
     }
  }

 }
