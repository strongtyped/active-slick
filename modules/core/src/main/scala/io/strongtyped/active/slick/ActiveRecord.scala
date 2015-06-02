package io.strongtyped.active.slick

import io.strongtyped.active.slick.dao.SlickDao
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext


trait ActiveRecord[M] {

  def model: M

  def dao: SlickDao[M]

  def save()(implicit exc: ExecutionContext): DBIO[M] = dao.save(model)

  def update()(implicit exc: ExecutionContext): DBIO[M] = dao.update(model)

  def delete()(implicit exc: ExecutionContext): DBIO[Int] = dao.delete(model)

}
