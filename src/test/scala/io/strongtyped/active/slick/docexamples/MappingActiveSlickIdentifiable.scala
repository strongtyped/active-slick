package io.strongtyped.active.slick.docexamples

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.{Beer, Identifiable}

import scala.slick.driver.{H2Driver, JdbcDriver}

trait MappingActiveSlickIdentifiable {
  this: ActiveSlick =>

  import jdbcDriver.simple._

  case class Foo(name: String, id: Option[Int] = None) extends Identifiable[Foo] {
    override type Id = Int
    override def withId(id: Id): Foo = copy(id = Some(id))
  }

  class FooTable(tag: Tag) extends IdTable[Foo, Int](tag, "FOOS") {
    def name = column[String]("NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, id.?) <>(Foo.tupled, Foo.unapply)
  }

  val Foos = new IdentifiableTableQuery[Foo, FooTable](tag => new FooTable(tag))

}


object MappingActiveSlickIdentifiableApp {

  class Components(override val jdbcDriver: JdbcDriver) extends ActiveSlick with MappingWithActiveSlick {
    import jdbcDriver.simple._
    val db = Database.forURL("jdbc:h2:mem:active-slick", driver = "org.h2.Driver")
  }
  object Components { val instance = new Components(H2Driver) }
  import Components.instance._

  def main(args:Array[String]) : Unit = {
    db.withTransaction { implicit sess =>
      val foo = Foo("foo")
      val fooWithId : Foo = Foos.save(foo)
      assert(fooWithId.id.isDefined, "Foo's ID should be defined")
    }
  }
}
