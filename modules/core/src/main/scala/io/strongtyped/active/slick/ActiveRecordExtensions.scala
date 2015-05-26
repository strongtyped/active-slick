package io.strongtyped.active.slick

import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

trait ActiveRecordExtensions { this: TableQueries with Profile =>

  trait ActiveRecord[M] {

    type TableQuery = ActiveTableQuery[M, _]

    def tableQuery: TableQuery
    def model: M

    def save()(implicit exc: ExecutionContext): DBIO[M] = tableQuery.save(model)

    def update()(implicit exc: ExecutionContext): DBIO[M] = tableQuery.update(model)

    def delete()(implicit exc: ExecutionContext): DBIO[Unit] = tableQuery.delete(model)
  }
}
