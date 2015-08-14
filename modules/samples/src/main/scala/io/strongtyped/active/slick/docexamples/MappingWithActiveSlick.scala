package io.strongtyped.active.slick.docexamples

import io.strongtyped.active.slick._
import slick.ast.BaseTypedType
import slick.driver.H2Driver
import io.strongtyped.active.slick.Lens._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

//@formatter:off
// tag::adoc[]
object MappingWithActiveSlick {

  case class Coffee(name: String, id: Option[Int] = None) extends Identifiable {
    override type Id = Int
  }

  object CoffeeRepo extends EntityActions(H2Driver) {

    import jdbcProfile.api._ // #<1>

    val baseTypedType: BaseTypedType[Id] = implicitly[BaseTypedType[Id]] // #<2>
    type Entity = Coffee // #<3>
    type EntityTable = CoffeeTable // # <4>
    val tableQuery = TableQuery[CoffeeTable] // # <5>

    def $id(table: CoffeeTable) = table.id // # <6>
    val idLens = lens[Coffee, Option[Int]]( // # <7>
      coffee => coffee.id,
      (coffee, id) => coffee.copy(id = id)
    )

    class CoffeeTable(tag: Tag) extends Table[Coffee](tag, "COFFEE") { // #<8>
      def name = column[String]("NAME")
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def * = (name, id.?) <>(Coffee.tupled, Coffee.unapply)
    }

  }

  implicit class EntryExtensions(val entity: Coffee) extends ActiveRecord[Coffee] {
    val crudActions = CoffeeRepo
  }

  val saveAction = Coffee("Colombia").save()
}
// end::adoc[]
//@formatter:on
