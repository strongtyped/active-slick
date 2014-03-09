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
    
    def add(person: Person) = query.returning(query.map(_.id)).insert(person)

    def create() =  query.ddl.create
  }


  implicit class PersonExtensions(person:Person) extends ActiveRecord[Person, Int](person) {
    def daoProvider(session:JdbcBackend#Session) = new PersonDao()(session)
  }
}

