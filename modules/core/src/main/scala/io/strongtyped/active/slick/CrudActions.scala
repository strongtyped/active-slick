package io.strongtyped.active.slick

import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext

trait CrudActions[M] {

  val jdbcProfile: JdbcProfile

  import jdbcProfile.api._

  type Model = M

  def count: DBIO[Int]

  def save(model: Model)(implicit exc: ExecutionContext): DBIO[Model]

  def update(model: Model)(implicit exc: ExecutionContext): DBIO[Model]

  def delete(model: Model)(implicit exc: ExecutionContext): DBIO[Int]

  def fetchAll(fetchSize: Int = 100)(implicit exc: ExecutionContext): StreamingDBIO[Seq[Model], Model]

}
