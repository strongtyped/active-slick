package io.strongtyped.active.slick.docexamples

import _root_.io.strongtyped.active.slick._
import slick.driver.H2Driver

import scala.language.postfixOps

//@formatter:off
object MappingActiveSlickIdentifiable extends JdbcProfileProvider {

  // tag::adoc[]
  val jdbcProfile = H2Driver

  import jdbcProfile.api._

  case class Foo(name: String, id: Option[Int] = None) extends Identifiable {
    override type Id = Int
  }

  object FooRepository extends EntityActions[Foo](jdbcProfile) {

    class FooTable(tag: Tag) extends jdbcProfile.api.Table[Foo](tag, "FOOS") {
      def name = column[String]("NAME")
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def * = (name, id.?) <>(Foo.tupled, Foo.unapply)
    }

    type EntityTable = FooTable // # <1>

    def $id(table: FooTable) = table.id // # <2>

    val tableQuery = TableQuery[FooTable] // # <3>

    val idLens = SimpleLens[Foo, Option[Int]](_.id, (foo, id) => foo.copy(id = id)) // # <4>

  }

  // end::adoc[]
}
//@formatter:on
