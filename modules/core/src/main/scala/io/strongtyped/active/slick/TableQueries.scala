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
import shapeless.Lens

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

trait TableQueries {
  this: Profile with Tables =>

  import jdbcDriver.simple._

  abstract class ActiveTableQuery[M, T <: Table[M]](cons: Tag => T) extends TableQuery(cons) {


    def count(implicit sess: Session): Int = length.run

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

  class TableWithIdQuery[M, I, T <: IdTable[M, I]](cons: Tag => T, idLens:Lens[M, Option[I]])(implicit bct: BaseColumnType[I])
      extends ActiveTableQuery[M, T](cons) {


    private def tryExtractId(model: M)(implicit sess: Session): Try[I] = {
      idLens.get(model) match {
        case Some(id) => Success(id)
        case None => Failure(RowNotFoundException(model))
      }
    }


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
      
      if (tried.isFailure && !sess.conn.getAutoCommit)
        sess.rollback()

      tried
    }

    protected def mustAffectAtLeastOneRow(query: => Int): Try[Unit] = {
      query match {
        case n if n >= 1 => Success(Unit)
        case 0 => Failure(NoRowsAffectedException)
      }
    }
    protected def mustAffectOneSingleRow(query: => Int): Try[Unit] = {
      query match {
        case 1 => Success(Unit)
        case 0 => Failure(NoRowsAffectedException)
        case n => Failure(ManyRowsAffectedException(n))
      }
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
        idLens.get(model) match {
          // if has an Id, try to update it
          case Some(id) => tryUpdate(id, model)

          // if has no Id, try to add it
          case None => tryAdd(model).map { id =>
            idLens.set(model)(Option(id))
          }
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




}
