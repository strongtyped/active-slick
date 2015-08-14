package io.strongtyped.active.slick

import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext

trait CrudActions {

  val jdbcProfile: JdbcProfile

  import jdbcProfile.api._

  type Entity

  def count: DBIO[Int]

  def save(entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity]

  def update(entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity]

  def delete(entity: Entity)(implicit exc: ExecutionContext): DBIO[Int]

  def fetchAll(fetchSize: Int = 100)(implicit exc: ExecutionContext): StreamingDBIO[Seq[Entity], Entity]

}
