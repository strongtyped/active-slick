package io.strongtyped.active.slick

import scala.slick.jdbc.JdbcBackend
import scala.util.Try


trait ActiveRecordExtensions { this:Profile with TableQueries with Tables =>

  import jdbcDriver.simple._

  trait ActiveRecord[M, T <: Table[M]] {

    def table: ActiveTableQuery[M, T]
    def model:M

    def save(implicit session: JdbcBackend#Session): M = table.save(model)
    def trySave(implicit session: JdbcBackend#Session): Try[M] = table.trySave(model)

    def delete(implicit session: JdbcBackend#Session): Boolean = table.delete(model)
    def tryDelete(implicit session: JdbcBackend#Session): Try[Boolean] = table.tryDelete(model)
  }
}
