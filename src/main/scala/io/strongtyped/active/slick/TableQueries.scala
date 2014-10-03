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

import io.strongtyped.active.slick.models.{Identifiable, Versionable}

import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.slick.jdbc.JdbcBackend

trait TableQueries { this:Profile with Tables =>

  import jdbcDriver.simple._

  abstract class ActiveTableQuery[M, T <: Table[M]](cons: Tag => T) extends TableQuery(cons) {

    def count(implicit sess:Session): Int = length.run

    def list(implicit sess:Session): List[M] = this.list

    def pagedList(pageIndex: Int, limit: Int)(implicit sess:Session): List[M] =
      drop(pageIndex).take(limit).run.toList

    def save(model: M)(implicit sess:Session): M
    def delete(model:M)(implicit sess:Session) : Boolean


  }

  abstract class TableWithIdQuery[M, I:BaseColumnType, T <: IdTable[M, I]](cons: Tag => T)
    extends ActiveTableQuery[M, T](cons) {

    /**
     * Extracts the model Id of a arbitrary model.
     * @param model a mapped model
     * @return a Some[I] if Id is filled, None otherwise
     */
    def extractId(model: M)(implicit sess: Session): Option[I]


    /**
     *
     * @param model a mapped model (usually without an assigned id).
     * @param id an id, usually generate by the database
     * @return a model M with an assigned id.
     */
    def withId(model: M, id: I)(implicit sess: Session): M


    def filterById(id: I)(implicit sess: Session) = filter(_.id === id)

    /**
     * Define an insert query that returns the database generated identifier.
     * @param model a mapped model
     * @return the database generated identifier.
     */
    def add(model: M)(implicit sess: Session): I =
      this.returning(this.map(_.id)).insert(model)


    override def save(model: M)(implicit sess: Session): M = {
      extractId(model)
      .map { id =>
        filterById(id).update(model)
        model
      }
      .getOrElse {
        withId(model, add(model))
      }
    }

    override def delete(model: M)(implicit sess: Session): Boolean =
      extractId(model).exists(id => deleteById(id))

    def deleteById(id: I)(implicit sess: Session): Boolean = filterById(id).delete == 1


    def findById(id: I)(implicit sess: Session): M = findOptionById(id).get

    def findOptionById(id: I)(implicit sess: Session): Option[M] = filterById(id).firstOption
  }

  class IdentifiableTableQuery[M <: Identifiable[M],  T <: IdTable[M, M#Id]](cons: Tag => T)(implicit ev1: BaseColumnType[M#Id])
    extends TableWithIdQuery[M, M#Id, T](cons) {

    def extractId(identifiable: M)
                 (implicit sess: JdbcBackend#Session) = identifiable.id

    def withId(entity: M, id: M#Id)
              (implicit sess: JdbcBackend#Session) = entity.withId(id)
  }

  class VersionableTableQuery[M <: Versionable[M] with Identifiable[M], T <: IdVersionTable[M, M#Id]](cons: Tag => T)(implicit ev1: BaseColumnType[M#Id])
    extends IdentifiableTableQuery[M, T](cons) {

    override def save(versionable: M)(implicit sess:Session): M = {
      val currentVersion = versionable.version
      val modelNewVersion = versionable.withVersion(System.currentTimeMillis())
      extractId(modelNewVersion)
      .map { id =>
        val q = filter(_.version === currentVersion).filter(_.id === id)

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

  object IdentifiableTableQuery {
    def apply[M <: Identifiable[M], T <: IdTable[M, M#Id]](cons: Tag => T)(implicit ev1: BaseColumnType[M#Id]) =
      new IdentifiableTableQuery[M, T](cons)
  }

  object VersionableTableQuery {
    def apply[M <: Versionable[M] with Identifiable[M], T <: IdVersionTable[M, M#Id]](cons: Tag => T)(implicit ev1: BaseColumnType[M#Id]) =
      new VersionableTableQuery[M, T](cons)
  }

  class StaleObjectStateException[T <: Versionable[T]](versionable:T)
    extends RuntimeException(s"Optimistic locking error - object in stale state: $versionable" )
}
