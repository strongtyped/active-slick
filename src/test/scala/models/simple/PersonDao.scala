package models.simple

import slick.dao.{SlickJdbcDao, IdentifiableTable}
import scala.slick.driver.H2Driver
import scala.slick.jdbc.JdbcBackend
import scala.slick.driver.H2Driver.simple._
import models.Person

/**
 * An example on how to implement a SlickJdbcDao for an simple case class.
 *
 * We only need to implement three methods.
 * {{{
 *   SlickJdbcDao.extractId, SlickJdbcDao.withId and SlickJdbcDao.queryById
 * }}}
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

  def extractId(person: Person): Option[Int] = person.id

  def withId(person: Person, id: Int): Person = person.copy(id = Option(id))

  def queryById(id: Int) = query.filter(_.id === id)
}