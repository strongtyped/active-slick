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

package slick.dao


trait SlickDao[M, I] {

  /**
   * Extracts the model Id of a arbitrary model.
   * @param model a mapped model
   * @return an Some[I] if Id is filled, None otherwise
   */
  def extractId(model: M): Option[I]

  /**
   *
   * @param model a mapped model (usually without an assigned id).
   * @param id an id, usually generate by the database
   * @return a model M with an assigned Id.
   */
  def withId(model: M, id: I): M

  def count: Int
  def save(model: M): M

  def delete(model: M): Boolean

  def deleteById(id: I): Boolean

  def findOptionById(id: I): Option[M]
  def findById(id: I): M = findOptionById(id).get

  def list : List[M]
  def pagedList(pageIndex: Int, limit: Int): List[M]

}