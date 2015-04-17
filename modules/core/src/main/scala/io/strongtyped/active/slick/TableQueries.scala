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
import slick.ast.BaseTypedType
import slick.dbio.{FailureAction, SuccessAction}

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.{Failure, Success}

trait TableQueries {
  this: Profile with Tables =>

  import driver.api._


  abstract class ActiveTableQuery[M, T <: Table[M]](cons: Tag => T) extends TableQuery(cons) {

    def count: DBIO[Int] = this.size.result

    def save(model: M)(implicit exc: ExecutionContext): DBIO[M]

    def update(model: M)(implicit exc: ExecutionContext): DBIO[M]

    def delete(model: M)(implicit exc: ExecutionContext): DBIO[Unit]

  }

  class TableWithIdQuery[M, I, T <: IdTable[M, I]](cons: Tag => T, idLens: Lens[M, Option[I]])
                                                  (implicit ev:BaseTypedType[I]) extends ActiveTableQuery[M, T](cons) {

    private def tryExtractId(model: M): DBIO[I] = {
      idLens.get(model) match {
        case Some(id) => SuccessAction(id)
        case None     => FailureAction(new RowNotFoundException(model))
      }
    }

    val filterById = this.findBy(_.id)

    def findById(id: I): DBIO[M] = filterById(id).result.head

    def findOptionById(id: I): DBIO[Option[M]] = filterById(id).result.headOption

    /**
     * Define an insert query that returns the database generated identifier.
     * @param model a mapped model
     * @return the database generated identifier.
     */
    def add(model: M): DBIO[I] = {
      this.returning(this.map(_.id)) += model
    }

    override def save(model: M)(implicit exc: ExecutionContext): DBIO[M] = {
      idLens.get(model) match {
        // if has an Id, try to update it
        case Some(id) => update(id, model)

        // if has no Id, try to add it
        case None => add(model).map { id =>
          idLens.set(model)(Option(id))
        }
      }
    }

    override def update(model: M)(implicit exc: ExecutionContext): DBIO[M] = {
      tryExtractId(model).flatMap { id =>
        update(id, model)
      }
    }

    protected def update(id: I, model: M)(implicit exc: ExecutionContext): DBIO[M] = {

      val triedUpdate = filterById(id).update(model).mustAffectOneSingleRow.asTry

      triedUpdate.map {
          case Success(_) => model
          case Failure(NoRowsAffectedException) => throw new RowNotFoundException(model)
          case Failure(ex) => throw ex
        }
    }

    override def delete(model: M)(implicit exc: ExecutionContext): DBIO[Unit] = {
      tryExtractId(model).flatMap { id =>
        deleteById(id)
      }
    }

    def deleteById(id: I)(implicit exc: ExecutionContext): DBIO[Unit] = {
      filterById(id).delete.mustAffectOneSingleRow.map(_ => Unit)
    }

  }

  implicit class UpdateActionExtensionMethods(dbAction: DBIO[Int]) {

    def mustAffectOneSingleRow(implicit exc: ExecutionContext): DBIO[Int] = {
      dbAction.flatMap {
        case 1          => dbAction // expecting one result
        case 0          => DBIO.failed(NoRowsAffectedException)
        case n if n > 1 => DBIO.failed(new TooManyRowsAffectedException(affectedRowCount = n, expectedRowCount = 1))
      }
    }

    def mustAffectAtLeastOneRow(implicit exc: ExecutionContext): DBIO[Int] = {

      dbAction.flatMap {
        case n if n >= 1 => dbAction // expecting one or more results
        case 0           => DBIO.failed(NoRowsAffectedException)
      }
    }
  }

}
