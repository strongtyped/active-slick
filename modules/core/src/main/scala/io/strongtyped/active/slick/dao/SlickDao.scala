package io.strongtyped.active.slick.dao

import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext

trait SlickDao[M] {

  val jdbcProfile:JdbcProfile

  import jdbcProfile.api._

  def count: DBIO[Int]

  def save(model: M)(implicit exc: ExecutionContext): DBIO[M]

  def update(model: M)(implicit exc: ExecutionContext): DBIO[M]

  def delete(model: M)(implicit exc: ExecutionContext): DBIO[Int]
}
