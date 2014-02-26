package models.component

import slick.dao.{ActiveRecord, SlickJdbcDao, IdentifiableTable}
import models.Person
import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted
import scala.slick.driver.JdbcProfile

trait PersonComponent {

  val profile:JdbcProfile

  class PersonDao(implicit val session:JdbcBackend#Session) extends SlickJdbcDao[Person, Int] {

    val profile = PersonComponent.this.profile
    import profile.simple._

    class Persons(tag: Tag) extends Table[Person](tag, "person2") with IdentifiableTable[Int] {

      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

      def firstName = column[String]("first_name")

      def lastName = column[String]("last_name")

      def * = (firstName, lastName, id.?) <>(Person.tupled, Person.unapply)
    }

    def query = lifted.TableQuery[Persons]

    def extractId(person: Person): Option[Int] = person.id

    def withId(person: Person, id: Int): Person = person.copy(id = Option(id))

    def queryById(id: Int) = query.filter(_.id === id)

    def create() =  query.ddl.create
  }


  implicit class PersonExtensions(person:Person) extends ActiveRecord[Person, Int](person) {
    def daoProvider(session:JdbcBackend#Session) = new PersonDao()(session)
  }
}

