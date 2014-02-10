package slick.dao


abstract class ActiveRecord[R, I](val row: R, val dao:SlickDao[R, I]) {

  def save: R = dao.save(row)
  def delete: Boolean = {
    dao.extractId(row) match {
      case Some(id) => dao.deleteById(id)
      case None => false
    }
  }
}
