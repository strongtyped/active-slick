package models.simple

import org.scalatest.{Matchers, FunSpec}
import models.TestDb.testDb
import scala.slick.driver.H2Driver.simple._

class PersonTest extends FunSpec with Matchers {

  describe("A Person") {
    it("should be persisted in DB") {

      testDb.withSession { implicit sess =>
        val personDao = new PersonDao
        personDao.query.ddl.create

        val initialCount = personDao.count

        val person = Person("John", "Smith")
        person.id should not be 'defined

        val persistedPerson = personDao.save(person)
        persistedPerson.id should be('defined)

        personDao.count should equal(initialCount + 1)

        personDao.delete(persistedPerson)
        personDao.count should equal(initialCount)
      }
    }
  }
}
