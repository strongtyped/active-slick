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

package models.entity

import slick.dao.IdentifiableJdbcDao


trait Entity[E <: Entity[E]] {
  // self-typing to E to force withId to return this type
  self: E =>

  def id: Option[Int]

  def withId(id: Int): E
}

abstract class EntityDao[E <: Entity[E], Long] extends IdentifiableJdbcDao[E, Int] {

  import profile.simple._

  def extractId(entity: E): Option[Int] = entity.id

  def withId(entity: E, id: Int): E = entity.withId(id)

  def queryById(id: Int) = query.filter(_.id === id)

}
