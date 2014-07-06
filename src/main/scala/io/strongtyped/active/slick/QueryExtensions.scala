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
import io.strongtyped.active.slick.models.{Versionable, Identifiable}
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

  abstract class BaseIdTableExt[T, I:BaseColumnType](query:TableQuery[_ <: Table[T] with TableWithId[I]])
    extends BaseTableExt(query) {

    /**
     * Extracts the model Id of a arbitrary model.
     * @param model a mapped model
     * @return a Some[I] if Id is filled, None otherwise
     */
    def extractId(model: T)(implicit sess:Session): Option[I]


    /**
     *
     * @param model a mapped model (usually without an assigned id).
     * @param id an id, usually generate by the database
     * @return a model T with an assigned id.
     */
    def withId(model: T, id: I)(implicit sess:Session): T


    def filterById(id: I)(implicit sess:Session) = query.filter(_.id === id)

    /**
     * Define an insert query that returns the database generated identifier.
     * @param model a mapped model
     * @return the database generated identifier.
     */
    def add(model: T)(implicit sess:Session): I =
      query.returning(query.map(_.id)).insert(model)


    override def save(model: T)(implicit sess:Session): T = {
      extractId(model)
      .map { id =>
        filterById(id).update(model)
        model
      }
      .getOrElse {
        withId(model, add(model))
      }
    }

    override def delete(model:T)(implicit sess:Session) : Boolean =
      extractId(model).exists(id => deleteById(id))

    def deleteById(id: I)(implicit sess:Session): Boolean = filterById(id).delete == 1


    def findById(id: I)(implicit sess:Session): T = findOptionById(id).get

    def findOptionById(id: I)(implicit sess:Session): Option[T] = filterById(id).firstOption
  }


  abstract class IdTableExt[T <: Identifiable[T]](query:TableQuery[_ <: Table[T] with TableWithId[T#Id] ])
                                                 (implicit ev1: BaseColumnType[T#Id]) extends BaseIdTableExt(query) {

    def extractId(identifiable: T)
                 (implicit sess: JdbcBackend#Session) = identifiable.id

    def withId(entity: T, id: T#Id)
              (implicit sess: JdbcBackend#Session) = entity.withId(id)
  }

  abstract class VersionableTableExt[T <: Versionable[T] with Identifiable[T]](query:TableQuery[_ <:  Table[T] with TableWithId[T#Id]  with TableWithVersion ])
                                                                              (implicit ev1: BaseColumnType[T#Id]) extends IdTableExt(query) {
    override def save(versionable: T)(implicit sess:Session): T = {
      val currentVersion = versionable.version
      val modelNewVersion = versionable.withVersion(System.currentTimeMillis())
      extractId(modelNewVersion)
      .map { id =>
        val q = query.filter(_.version === currentVersion).filter(_.id === id)

        if (q.length.run != 1)
          throw new StaleObjectStateException(versionable)

        q.update(modelNewVersion)
        modelNewVersion
      }
      .getOrElse {
        withId(modelNewVersion, add(modelNewVersion))
      }
    }

  }

  class StaleObjectStateException[T <: Versionable[T]](versionable:T)
    extends RuntimeException(s"Optimistic locking error - object in stale state: $versionable" )
}