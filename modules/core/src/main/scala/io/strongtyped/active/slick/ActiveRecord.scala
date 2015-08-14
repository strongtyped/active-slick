package io.strongtyped.active.slick

import slick.dbio.DBIO
import scala.concurrent.ExecutionContext


trait ActiveRecord[E] {

  def entity: E

  val repository: CrudActions

  private val casted: repository.Model = entity.asInstanceOf[repository.Model]

  def save()(implicit exc: ExecutionContext): DBIO[E] =
    repository.save(casted).map(_.asInstanceOf[E])

  def update()(implicit exc: ExecutionContext): DBIO[E] =
    repository.update(casted).map(_.asInstanceOf[E])

  def delete()(implicit exc: ExecutionContext): DBIO[Int] = repository.delete(casted)

}
