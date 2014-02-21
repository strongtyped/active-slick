package slick.dao

import scala.slick.jdbc.JdbcBackend

abstract class ActiveRecord[R, I](val row: R, val daoProvider: JdbcBackend#Session => SlickDao[R, I]) {

  def save(implicit session:JdbcBackend#Session): R = daoProvider(session).save(row)

  def delete(implicit session:JdbcBackend#Session): Boolean = {
    val dao = daoProvider(session)
    dao.extractId(row) match {
      case Some(id) => dao.deleteById(id)
      case None => false
    }
  }

}