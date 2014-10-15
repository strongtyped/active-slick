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

import io.strongtyped.active.slick.exceptions._
import io.strongtyped.active.slick.models.{ Identifiable, Versionable }
import scala.language.implicitConversions
import scala.slick.jdbc.JdbcBackend
import scala.util.{ Success, Failure, Try }

trait TableQueries {
  this: Profile with Tables =>

  import jdbcDriver.simple._

  abstract class ActiveTableQuery[M, T <: Table[M]](cons: Tag => T) extends TableQuery(cons) {

    def count(implicit sess: Session): Int = length.run

    def fetchAll(implicit sess: Session): List[M] = this.list

    def pagedList(pageIndex: Int, limit: Int)(implicit sess: Session): List[M] =
      drop(pageIndex).take(limit).run.toList

    def save(model: M)(implicit sess: Session): M = trySave(model).get

    def update(model: M)(implicit sess: Session): M = tryUpdate(model).get

    def delete(model: M)(implicit sess: Session): Unit = tryDelete(model).get

    /**
     * Try to save the model.
     * @return A Success[M] is case of success, Failure otherwise.
     */
    def trySave(model: M)(implicit sess: Session): Try[M]

    /**
     * Try to delete the model.
     * @return A Success[Unit] is case of success, Failure otherwise.
     */
    def tryDelete(model: M)(implicit sess: Session): Try[Unit]

    /**
     * Try to update the model.
     * @return A Success[M] is case of success, Failure otherwise.
     */
    def tryUpdate(model: M)(implicit sess: Session): Try[M]

  }

  abstract class TableWithIdQuery[M, I: BaseColumnType, T <: IdTable[M, I]](cons: Tag => T)
      extends ActiveTableQuery[M, T](cons) {

    /**
     * Extracts the model Id of a arbitrary model.
     * @param model a mapped model
     * @return a Some[I] if Id is filled, None otherwise
     */
    def extractId(model: M)(implicit sess: Session): Option[I]

    def tryExtractId(model: M)(implicit sess: Session): Try[I] = {
      extractId(model) match {
        case Some(id) => Success(id)
        case None => Failure(RowNotFoundException(model))
      }
    }

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
    def add(model: M)(implicit sess: Session): I = tryAdd(model).get

    def tryAdd(model: M)(implicit sess: Session): Try[I] = {
      rollbackOnFailure {
        Try(this.returning(this.map(_.id)).insert(model))
      }
    }

    protected def rollbackOnFailure[R](query: => Try[R])(implicit sess: Session): Try[R] = {
      val tried = query
      if (tried.isFailure) sess.rollback()
      tried
    }

    protected def mustAffectOneSingleRow(query: => Int): Try[Unit] = {

      val affectedRows = query

      if (affectedRows == 1) Success(Unit)
      else if (affectedRows == 0) Failure(NoRowsAffectedException)
      else Failure(ManyRowsAffectedException(affectedRows))

    }

    override def tryUpdate(model: M)(implicit sess: Session): Try[M] = {
      rollbackOnFailure {
        tryExtractId(model).flatMap { id =>
          tryUpdate(id, model)
        }
      }
    }

    override def trySave(model: M)(implicit sess: Session): Try[M] = {
      rollbackOnFailure {
        extractId(model) match {
          // if has an Id, try to update it
          case Some(id) => tryUpdate(id, model)

          // if has no Id, try to add it
          case None => tryAdd(model).map { id => withId(model, id) }
        }
      }
    }

    protected def tryUpdate(id: I, model: M)(implicit sess: Session): Try[M] = {
      mustAffectOneSingleRow {
        filterById(id).update(model)
      }.recoverWith {
        // if nothing gets updated, we want a Failure[RowNotFoundException]
        // all other failures must be propagated
        case NoRowsAffectedException => Failure(RowNotFoundException(model))

      }.map { _ =>
        model // return a Try[M] if only one row is affected
      }
    }

    override def tryDelete(model: M)(implicit sess: Session): Try[Unit] = {
      rollbackOnFailure {
        tryExtractId(model).flatMap { id =>
          tryDeleteById(id)
        }
      }
    }

    def deleteById(id: I)(implicit sess: Session): Unit = tryDeleteById(id).get

    def tryDeleteById(id: I)(implicit sess: Session): Try[Unit] = {
      rollbackOnFailure {
        mustAffectOneSingleRow {
          filterById(id).delete

        }.recoverWith {
          // if nothing gets deleted, we want a Failure[RowNotFoundException]
          // all other failures must be propagated
          case NoRowsAffectedException => Failure(RowNotFoundException(id))
        }
      }
    }

    def tryFindById(id: I)(implicit sess: Session): Try[M] = {
      findOptionById(id) match {
        case Some(model) => Success(model)
        case None => Failure(RowNotFoundException(id))
      }
    }

    def findById(id: I)(implicit sess: Session): M = findOptionById(id).get

    def findOptionById(id: I)(implicit sess: Session): Option[M] = filterById(id).firstOption
  }

  class EntityTableQuery[M <: Identifiable[M], T <: EntityTable[M]](cons: Tag => T)(implicit ev1: BaseColumnType[M#Id])
      extends TableWithIdQuery[M, M#Id, T](cons) {

    def extractId(identifiable: M)(implicit sess: JdbcBackend#Session) = identifiable.id

    def withId(entity: M, id: M#Id)(implicit sess: JdbcBackend#Session) = entity.withId(id)
  }

  class VersionableEntityTableQuery[M <: Versionable[M] with Identifiable[M], T <: VersionableEntityTable[M]](cons: Tag => T)(implicit ev1: BaseColumnType[M#Id])
      extends EntityTableQuery[M, T](cons) {

    override protected def tryUpdate(id: M#Id, versionable: M)(implicit sess: Session): Try[M] = {

      val queryById = filter(_.id === id)
      val queryByIdAndVersion = queryById.filter(_.version === versionable.version)
      val modelWithNewVersion = versionable.withVersion(versionable.version + 1)

      mustAffectOneSingleRow {
        queryByIdAndVersion.update(modelWithNewVersion)

      }.recoverWith {
        // no updates?
        case NoRowsAffectedException =>
          // if row exists we have a stale object
          // all other failures must be propagated
          tryFindById(id).flatMap { currentOnDb =>
            Failure(StaleObjectStateException(versionable, currentOnDb))
          }

      }.map { _ =>
        modelWithNewVersion // return the versionable entity with an updated version
      }
    }

    override def trySave(versionable: M)(implicit sess: Session): Try[M] = {
      rollbackOnFailure {
        extractId(versionable) match {
          // if has an Id, try to update it
          case Some(id) => tryUpdate(id, versionable)

          // if has no Id, try to add it
          case None =>
            // init versioning
            val modelWithVersion = versionable.withVersion(1)
            tryAdd(modelWithVersion).map { id => withId(modelWithVersion, id) }
        }
      }
    }

  }

  object EntityTableQuery {
    def apply[M <: Identifiable[M], T <: EntityTable[M]](cons: Tag => T)(implicit ev1: BaseColumnType[M#Id]) =
      new EntityTableQuery[M, T](cons)
  }

  object VersionableEntityTableQuery {
    def apply[M <: Versionable[M] with Identifiable[M], T <: VersionableEntityTable[M]](cons: Tag => T)(implicit ev1: BaseColumnType[M#Id]) =
      new VersionableEntityTableQuery[M, T](cons)
  }

}
