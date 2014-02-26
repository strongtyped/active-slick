/*
 * Copyright 2014 Renato Guerra Cavalcanti (@renatocaval)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.simple

import org.scalatest.{Matchers, FunSpec}
import models.TestDb.testDb
import scala.slick.driver.H2Driver.simple._
import models.Person

class PersonDaoTest extends FunSpec with Matchers {

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
