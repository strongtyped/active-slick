package models.simple

import slick.dao.{ActiveRecord, SlickJdbcDao, IdentifiableTable}
import scala.slick.driver.H2Driver
import scala.slick.jdbc.JdbcBackend
import scala.slick.driver.H2Driver.simple._

case class Person(firstName: String, lastName: String, id: Option[Int] = None)

/**
 * An example on how to implement a SlickJdbcDao for an simple case class.
 *
 * We only need to implement two methods. SlickJdbcDao.extractId and SlickJdbcDao.withId.
 */
class PersonDao(implicit val session: JdbcBackend#Session) extends SlickJdbcDao[Person, Int] {

  val profile = H2Driver

  class Persons(tag: Tag) extends Table[Person](tag, "person") with IdentifiableTable[Int] {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("first_name")

    def lastName = column[String]("last_name")

    def * = (firstName, lastName, id.?) <>(Person.tupled, Person.unapply)
  }

  def query = TableQuery[Persons]

  def extractId(row: Person): Option[Int] =
    row.id

  def withId(row: Person, id: Int): Person =
    row.copy(id = Option(id))

}