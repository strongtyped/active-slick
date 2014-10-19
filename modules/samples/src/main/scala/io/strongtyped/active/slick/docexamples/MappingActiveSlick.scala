package io.strongtyped.active.slick.docexamples

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.Identifiable

import scala.slick.driver.{ H2Driver, JdbcDriver }

trait MappingWithActiveSlick {
  this: ActiveSlick =>

  import jdbcDriver.simple._

  case class Foo(name: String, id: Option[Int] = None)

  class FooTable(tag: Tag) extends IdTable[Foo, Int](tag, "FOOS") {
    def name = column[String]("NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, id.?) <> (Foo.tupled, Foo.unapply)
  }

  val Foos = new TableWithIdQuery[Foo, Int, FooTable](tag => new FooTable(tag)) {
    override def extractId(model: Foo) = model.id
    override def withId(model: Foo, id: Int) = model.copy(id = Some(id))
  }

}

object MappingWithActiveSlickApp {

  class Components(override val jdbcDriver: JdbcDriver) extends ActiveSlick with MappingWithActiveSlick {
    import jdbcDriver.simple._
    val db = Database.forURL("jdbc:h2:mem:active-slick", driver = "org.h2.Driver")
    def createSchema(implicit sess: Session) = Foos.ddl.create
  }
  object Components { val instance = new Components(H2Driver) }
  import Components.instance._

  def main(args: Array[String]): Unit = {
    db.withTransaction { implicit sess =>
      createSchema
      val foo = Foo("foo")
      val fooWithId: Foo = Foos.save(foo)
      assert(fooWithId.id.isDefined, "Foo's ID should be defined")
    }
  }
}
