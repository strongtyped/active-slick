package io.strongtyped.active.slick

import slick.dbio.DBIO
import scala.concurrent.ExecutionContext


trait ActiveRecord[E] {

  def entity: E

  val crudActions: CrudActions

  private val casted: crudActions.Entity = entity.asInstanceOf[crudActions.Entity]

  def save()(implicit exc: ExecutionContext): DBIO[E] =
    crudActions.save(casted).map(_.asInstanceOf[E])

  def update()(implicit exc: ExecutionContext): DBIO[E] =
    crudActions.update(casted).map(_.asInstanceOf[E])

  def delete()(implicit exc: ExecutionContext): DBIO[Int] = crudActions.delete(casted)

}
