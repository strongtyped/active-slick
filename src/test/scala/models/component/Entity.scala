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

package models.component

import slick.dao.SlickJdbcDao
import scala.slick.driver.JdbcProfile


trait Entity[E <: Entity[E, I], I] {
  // self-typing to E to force withId to return this type
  self: E =>

  def id: Option[I]

  def withId(id: I): E
}


abstract class EntityDao[E <: Entity[E, I], I: JdbcProfile#BaseColumnType] extends SlickJdbcDao[E, I] {

  def extractId(entity: E): Option[I] = entity.id

  def withId(entity: E, id: I): E = entity.withId(id)

}