package slick.dao

import scala.slick.jdbc.JdbcBackend

abstract class ActiveRecord[M, I](val model: M, val daoProvider: JdbcBackend#Session => SlickDao[M, I]) {

  def save(implicit session:JdbcBackend#Session): M =
    daoProvider(session).save(model)

  def delete(implicit session:JdbcBackend#Session): Boolean =
    daoProvider(session).delete(model)

}