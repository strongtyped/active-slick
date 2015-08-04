package io.strongtyped.active.slick

import slick.dbio.DBIO
import scala.concurrent.ExecutionContext


trait ActiveRecord[M] {

  def model: M

  def crudActions: CrudActions[M]

  def save()(implicit exc: ExecutionContext): DBIO[M] = crudActions.save(model)

  def update()(implicit exc: ExecutionContext): DBIO[M] = crudActions.update(model)

  def delete()(implicit exc: ExecutionContext): DBIO[Int] = crudActions.delete(model)

}
