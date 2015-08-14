package io.strongtyped.active.slick.docexamples

import scala.language.postfixOps

//@formatter:off
object MappingWithoutActiveSlick {

  // tag::adoc[]
  import slick.driver.H2Driver.api._

  case class Coffee(name: String, id: Option[Int] = None) // #<1>

  class CoffeeTable(tag: Tag) extends Table[Coffee](tag, "COFFEE") {
    def name = column[String]("NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc) // #<2>
    def * = (name, id.?) <>(Coffee.tupled, Coffee.unapply)
  }

  val Coffees = TableQuery[CoffeeTable]

  val coffee = Coffee("Colombia")
  val insertAction = Coffees.returning(Coffees.map(_.id)) += coffee // #<3>
  // end::adoc[]
}
//@formatter:on