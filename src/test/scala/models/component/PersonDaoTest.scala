package models.component


import org.scalatest.{Matchers, FunSpec}
import models.TestDb.testDb
import models.Person
import Components.instance._

class PersonDaoTest extends FunSpec with Matchers {

  describe("A Person") {
    it("should be persisted in DB") {

      testDb.withSession { implicit sess =>
        val personDao = new PersonDao
        personDao.create()

        val initialCount = personDao.count

        val person = Person("John", "Smith")
        person.id should not be 'defined

        val persistedPerson = person.save

        persistedPerson.id should be('defined)

        personDao.count should equal(initialCount + 1)

        personDao.delete(persistedPerson)
        personDao.count should equal(initialCount)
      }
    }
  }
}
