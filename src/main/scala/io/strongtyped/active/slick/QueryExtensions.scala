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

import scala.slick.jdbc.JdbcBackend
import io.strongtyped.active.slick.models.Entity
import scala.language.implicitConversions

trait QueryExtensions { this:Profile with Tables =>

  import jdbcDriver.simple._

  abstract class BaseTableExt[M](query:TableQuery[_ <: Table[M]]) {
    def count(implicit sess:Session): Int =
      query.length.run

    def list(implicit sess:Session): List[M] = query.list

    def pagedList(pageIndex: Int, limit: Int)(implicit sess:Session): List[M] =
      query.drop(pageIndex).take(limit).run.toList

    def save(model: M)(implicit sess:Session): M
    def delete(model:M)(implicit sess:Session) : Boolean
  }

  abstract class IdentifiableTableExt[M, I:BaseColumnType](query:TableQuery[_ <: IdentifiableTable[M, I]])
    extends BaseTableExt(query) {

    /**
     * Extracts the model Id of a arbitrary model.
     * @param model a mapped model
     * @return a Some[I] if Id is filled, None otherwise
     */
    def extractId(model: M)(implicit sess:Session): Option[I]


    /**
     *
     * @param model a mapped model (usually without an assigned id).
     * @param id an id, usually generate by the database
     * @return a model M with an assigned id.
     */
    def withId(model: M, id: I)(implicit sess:Session): M


    def filterById(id: I)(implicit sess:Session) = query.filter(_.id === id)

    /**
     * Define an insert query that returns the database generated identifier.
     * @param model a mapped model
     * @return the database generated identifier.
     */
    def add(model: M)(implicit sess:Session): I =
      query.returning(query.map(_.id)).insert(model)


    override def save(model: M)(implicit sess:Session): M = {
      extractId(model)
      .map { id =>
        filterById(id).update(model)
        model
      }
      .getOrElse {
        withId(model, add(model))
      }
    }

    override def delete(model:M)(implicit sess:Session) : Boolean =
      extractId(model).exists(id => deleteById(id))

    def deleteById(id: I)(implicit sess:Session): Boolean = filterById(id).delete == 1


    def findById(id: I)(implicit sess:Session): M = findOptionById(id).get

    def findOptionById(id: I)(implicit sess:Session): Option[M] = filterById(id).firstOption
  }

  abstract class EntityTableExt[E <: Entity[E]](query:TableQuery[_ <: IdentifiableTable[E, E#Id]])
                                               (implicit ev1: BaseColumnType[E#Id]) extends IdentifiableTableExt(query) {

    def extractId(entity: E)
                 (implicit sess: JdbcBackend#Session) = entity.id

    def withId(entity: E, id: E#Id)
              (implicit sess: JdbcBackend#Session) = entity.withId(id)
  }


}