package io.strongtyped.active.slick.docexamples

import io.strongtyped.active.slick._
import slick.ast.BaseTypedType
import slick.driver.H2Driver
import io.strongtyped.active.slick.Lens._
import scala.language.postfixOps

//@formatter:off
// tag::adoc[]
object MappingWithActiveSlick {

  case class Coffee(name: String, id: Option[Int] = None) extends Identifiable {
    override type Id = Int
  }

  object CoffeeRepo extends EntityActions(H2Driver) {

    import jdbcProfile.api._

    class CoffeeTable(tag: Tag) extends Table[Coffee](tag, "COFFEE") {
      def name = column[String]("NAME")
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def * = (name, id.?) <>(Coffee.tupled, Coffee.unapply)
    }

    implicit val baseTypedType: BaseTypedType[Id] = implicitly[BaseTypedType[Id]] // #<1>
    type Entity = Coffee // #<2>
    type EntityTable = CoffeeTable // # <3>
    def $id(table: CoffeeTable) = table.id // # <4>
    val tableQuery = TableQuery[CoffeeTable] // # <5>
    val idLens = lens[Coffee, Option[Int]]( // # <6>
      coffee => coffee.id,
      (coffee, id) => coffee.copy(id = id)
    )
  }

  implicit class EntryExtensions(val entity: Coffee) extends ActiveRecord[Coffee] {
    val crudActions = CoffeeRepo
  }

  val saveAction = Coffee("Colombia").save()
}
// end::adoc[]
//@formatter:on
