package io.strongtyped.active.slick

import io.strongtyped.active.slick.exceptions.{TooManyRowsAffectedException, NoRowsAffectedException}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

object DBIOExtensions {

  implicit class UpdateActionExtensionMethods(dbAction: DBIO[Int]) {

    def mustAffectOneSingleRow(implicit exc: ExecutionContext): DBIO[Int] = {
      dbAction.flatMap {
        case 1          => DBIO.successful(1) // expecting one result
        case 0          => DBIO.failed(NoRowsAffectedException)
        case n if n > 1 => DBIO.failed(new TooManyRowsAffectedException(affectedRowCount = n, expectedRowCount = 1))
      }
    }

    def mustAffectAtLeastOneRow(implicit exc: ExecutionContext): DBIO[Int] = {

      dbAction.flatMap {
        case n if n >= 1 => DBIO.successful(n) // expecting one or more results
        case 0           => DBIO.failed(NoRowsAffectedException)
      }
    }
  }

  implicit class SelectSingleExtensionMethods[R](dbAction: DBIO[Seq[R]]) {

    def mustSelectSingleRecord(implicit exc: ExecutionContext): DBIO[R] = {
      dbAction.flatMap {
        case s if s.size == 1 => DBIO.successful(s.head)
        case s if s.isEmpty   => DBIO.failed(NoRowsAffectedException)
        case s                => DBIO.failed(new TooManyRowsAffectedException(affectedRowCount = s.size, expectedRowCount = 1))
      }
    }
  }

}
