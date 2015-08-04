package io.strongtyped.active.slick

import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext


trait EntityActionsLike[M <: Identifiable] extends CrudActions[M] {

  val jdbcProfile: JdbcProfile

  import jdbcProfile.api._

  type Entity = M
  type Id = M#Id

  def insert(entity: Entity)(implicit exc: ExecutionContext): DBIO[Id]

  def deleteById(id: Id)(implicit exc: ExecutionContext): DBIO[Int]

  def findById(id: Id): DBIO[Entity]

  def findOptionById(id: Id): DBIO[Option[Entity]]

}
