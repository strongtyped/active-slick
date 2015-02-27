package io.strongtyped.active.slick.docexamples

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.Identifiable
import shapeless._

import slick.driver.{H2Driver, JdbcDriver}

trait MappingActiveSlickIdentifiable {
  this: ActiveSlick =>

  import jdbcDriver.simple._

  case class Foo(name: String, id: Option[Int] = None) extends Identifiable {
    override type Id = Int
  }

  class FooTable(tag: Tag) extends EntityTable[Foo](tag, "FOOS") {
    def name = column[String]("NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, id.?) <> (Foo.tupled, Foo.unapply)
  }

  val Foos = EntityTableQuery[Foo, FooTable](
    cons = tag => new FooTable(tag),
    idLens = lens[Foo] >> 'id
  )

}

object MappingActiveSlickIdentifiableApp {

  class Components(override val jdbcDriver: JdbcDriver) extends ActiveSlick with MappingActiveSlickIdentifiable {
    import jdbcDriver.simple._
    val db = Database.forURL("jdbc:h2:mem:active-slick", driver = "org.h2.Driver")
    def createSchema(implicit sess: Session) = Foos.ddl.create
  }

  object Components { val instance = new Components(H2Driver) }

  import io.strongtyped.active.slick.docexamples.MappingActiveSlickIdentifiableApp.Components.instance._

  def main(args: Array[String]): Unit = {
    db.withTransaction { implicit sess =>
      createSchema
      val foo = Foo("foo")
      val fooWithId: Foo = Foos.save(foo)
      assert(fooWithId.id.isDefined, "Foo's ID should be defined")
    }
  }
}
