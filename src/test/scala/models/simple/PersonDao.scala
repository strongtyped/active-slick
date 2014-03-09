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

import slick.dao.{IdentifiableJdbcDao, IdentifiableTable}
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
class PersonDao(implicit val session: JdbcBackend#Session) extends IdentifiableJdbcDao[Person, Int] {

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