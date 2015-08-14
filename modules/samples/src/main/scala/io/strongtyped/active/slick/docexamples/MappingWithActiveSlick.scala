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

  case class Coffee(name: String, id: Option[Int] = None)

  object CoffeeRepo extends EntityActions(H2Driver) {

    import jdbcProfile.api._ // #<1>

    type Entity = Coffee // #<2>
    type Id = Int // #<3>
    type EntityTable = CoffeeTable // # <4>
    val tableQuery = TableQuery[CoffeeTable] // # <6>

    def $id(table: CoffeeTable) = table.id // # <7>
    val idLens = lens[Coffee, Option[Int]]( // # <8>
      coffee => coffee.id,
      (coffee, id) => coffee.copy(id = id)
    )

    val baseTypedType: BaseTypedType[Id] = implicitly[BaseTypedType[Id]] // #<8>

    class CoffeeTable(tag: Tag) extends Table[Coffee](tag, "COFFEE") { // #<9>
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
