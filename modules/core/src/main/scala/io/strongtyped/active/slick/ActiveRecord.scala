package io.strongtyped.active.slick

import slick.dbio.DBIO
import scala.concurrent.ExecutionContext


abstract class ActiveRecord[R <: CrudActions](val repository: R) {

  def model: repository.Model

  def save()(implicit exc: ExecutionContext): DBIO[repository.Model] =
    repository.save(model)

  def update()(implicit exc: ExecutionContext): DBIO[repository.Model] =
    repository.update(model)

  def delete()(implicit exc: ExecutionContext): DBIO[Int] = repository.delete(model)

}
