package io.strongtyped.active.slick.docexamples

import scala.language.postfixOps

//@formatter:off
object MappingWithoutActiveSlick {

  // tag::adoc[]
  import slick.driver.H2Driver.api._

  case class Foo(name: String, id: Option[Int] = None) // #<1>

  class FooTable(tag: Tag) extends Table[Foo](tag, "FOOS") {
    def name = column[String]("NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc) // #<2>
    def * = (name, id.?) <>(Foo.tupled, Foo.unapply)
  }

  val Foos = TableQuery[FooTable]

  val foo = Foo("foo")
  val insertAction = Foos.returning(Foos.map(_.id)) += foo // #<3>
  // end::adoc[]
}
//@formatter:on