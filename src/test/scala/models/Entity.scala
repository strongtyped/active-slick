package models

import slick.dao.SlickJdbcDao
import scala.slick.driver.JdbcProfile


trait Entity[E <: Entity[E, I], I] {
  // self-typing to E to force withId to return this type
  self: E =>

  def id: Option[I]

  def withId(id: I): E
}


abstract class EntityDao[E <: Entity[E, I], I: JdbcProfile#BaseColumnType] extends SlickJdbcDao[E, I] {

  def extractId(entity: E): Option[I] = entity.id

  def withId(entity: E, id: I): E = entity.withId(id)

}