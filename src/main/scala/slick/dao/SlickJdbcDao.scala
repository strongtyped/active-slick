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

import scala.languageFeature.implicitConversions
import scala.slick.jdbc.JdbcBackend
import scala.slick.driver.JdbcProfile
import scala.slick.lifted.Column
import scala.slick.lifted



trait SlickJdbcDao[M, I] extends SlickDao[M, I] {

  val profile:JdbcProfile

  import profile.simple._

  implicit val session:JdbcBackend#Session

  def query: TableQuery[_ <: Table[M]]


  /**
   *
   * @param model a mapped model (usually without an assigned id).
   * @param id an id, usually generate by the database
   * @return a model M with an assigned Id.
   */
  def withId(model: M, id: I): M


  /**
   * Defined the base query to find object by id.
   *
   * @param id
   * @return
   */
  def queryById(id: I): Query[Table[M], M]

  /**
   * Define an insert query that returns the database generated identifier.
   * @param model a mapped model
   * @return the database generated identifier.
   */
  def add(model: M): I


  def count: Int = query.length.run

  def save(model: M): M =
    extractId(model) match {
      case Some(id) => queryById(id).update(model); model
      case None => withId(model, add(model))
    }

  def delete(model:M) : Boolean =
    extractId(model) match {
      case Some(id) => deleteById(id)
      case None => false
    }


  def deleteById(id: I): Boolean = queryById(id).delete == 1

  def findOptionById(id: I): Option[M] = queryById(id).firstOption


  def list: List[M] = query.list

  def pagedList(pageIndex: Int, limit: Int): List[M] =
    query.drop(pageIndex).take(limit).run.toList

}
