package models

import slick.dao.SlickJdbcDao

case class Person(firstName:String, lastName:String, id:Option[Int] = None)

/**
 * An example on how to implement a SlickJdbcDao for an simple case class.
 *
 * We only need to implement two methods. SlickJdbcDao.extractId and SlickJdbcDao.withId.
 */
class PersonDao extends SlickJdbcDao[Person, Int] {
  def extractId(row: Person): Option[Int] =
    row.id

  def withId(row: Person, id: Int): Person =
    row.copy(id = Option(id))
}
