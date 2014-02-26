package slick.dao

import scala.slick.jdbc.JdbcBackend

abstract class ActiveRecord[M, I](val model: M) {

  def save(implicit session:JdbcBackend#Session): M =
    daoProvider(session).save(model)

  def delete(implicit session:JdbcBackend#Session): Boolean =
    daoProvider(session).delete(model)

  def daoProvider(session: JdbcBackend#Session): SlickDao[M, I]
}