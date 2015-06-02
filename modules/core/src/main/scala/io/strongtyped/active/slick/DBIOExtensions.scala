package io.strongtyped.active.slick

import io.strongtyped.active.slick.exceptions.{TooManyRowsAffectedException, NoRowsAffectedException}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

object DBIOExtensions {

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
