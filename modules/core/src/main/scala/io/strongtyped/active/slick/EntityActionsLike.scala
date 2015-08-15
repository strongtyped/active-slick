package io.strongtyped.active.slick

import slick.ast.BaseTypedType
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext

/**
 * Define basic life cycle actions for a Entity that involve.
 *
 */
trait EntityActionsLike extends CrudActions {

  val jdbcProfile: JdbcProfile

  import jdbcProfile.api._

  /** The type of the Entity */
  type Entity
  /** The `Entity`'s Id type */
  type Id
  /** CrudActions.Model is the `Entity` in this context */
  type Model = Entity

  // tag::adoc[]
  /** Insert a new `Entity`
    * @return DBIO[Id] for the generated `Id`
    */
  def insert(entity: Entity)(implicit exc: ExecutionContext): DBIO[Id]

  /** Delete a `Entity` by `Id`
    * @return DBIO[Int] with the number of affected rows
    */
  def deleteById(id: Id)(implicit exc: ExecutionContext): DBIO[Int]

  /** Finds `Entity` referenced by `Id`.
    * May fail if no `Entity` is found for passed `Id`
    * @return DBIO[Entity] for the `Entity`
    */
  def findById(id: Id): DBIO[Entity]

  /** Finds `Entity` referenced by `Id` optionally.
    * @return DBIO[Option[Entity]] for the `Entity`
    */
  def findOptionById(id: Id): DBIO[Option[Entity]]

  // end::adoc[]
}
