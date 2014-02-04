package models

import slick.dao.{BaseDaoComponent, IdentifiableTable}
import scala.slick.driver.JdbcProfile
import scala.slick.lifted

case class Person(firstName: String, lastName: String, id: Option[Int] = None)

/**
 * An example on how to implement a SlickJdbcDao for an simple case class.
 *
 * We only need to implement two methods. SlickJdbcDao.extractId and SlickJdbcDao.withId.
 */
trait PersonComponent extends BaseDaoComponent {

  import profile.simple._

  object PersonDao extends SlickJdbcDao[Person, Int] {

    def query = TableQuery[Persons]

    class Persons(tag: Tag) extends profile.simple.Table[Person](tag, "person") with IdentifiableTable[Int] {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def firstName = column[String]("first_name")
      def lastName = column[String]("last_name")
      def * = (firstName, lastName, id.?) <> (Person.tupled, Person.unapply)
    }


    def extractId(row: Person): Option[Int] =
      row.id

    def withId(row: Person, id: Int): Person =
      row.copy(id = Option(id))
  }

  implicit class PersonExtensions(val person:Person) extends PersonDao.ActiveRecord(person)
}

