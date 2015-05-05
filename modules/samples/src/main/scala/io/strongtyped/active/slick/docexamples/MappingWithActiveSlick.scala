package io.strongtyped.active.slick.docexamples

import io.strongtyped.active.slick.{SimpleLens, ActiveSlick}
import slick.driver.{H2Driver, JdbcDriver}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

trait MappingWithActiveSlick {
  this: ActiveSlick =>

  import driver.api._

  case class Foo(name: String, id: Option[Int] = None)
  
  class FooTable(tag: Tag) extends IdTable[Foo, Int](tag, "FOOS") {
    def name = column[String]("NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, id.?) <> (Foo.tupled, Foo.unapply)
  }

  val Foos = new TableWithIdQuery[Foo, Int, FooTable](
    tag => new FooTable(tag),
    idLens = SimpleLens[Foo, Option[Int]](_.id, (foo, id) => foo.copy(id = id))
  )
}

object MappingWithActiveSlickApp {

  class Components(override val driver: JdbcDriver) extends ActiveSlick with MappingWithActiveSlick {
    import driver.api._

    def run[T](block: Database => T): T = {
      val db = Database.forURL("jdbc:h2:mem:active-slick", driver = "org.h2.Driver")
      try {
        Await.ready(db.run(Foos.schema.create), 200 millis)
        block(db)
      } finally {
        db.close()
      }
    }

  }
  object Components { val instance = new Components(H2Driver) }
  import io.strongtyped.active.slick.docexamples.MappingWithActiveSlickApp.Components.instance._

  def main(args: Array[String]): Unit = {
      run { db =>
        val foo = Foo("foo")
        val fooWithId = Await.result(db.run(Foos.save(foo)), 200 millis)
        assert(fooWithId.id.isDefined, "Foo's ID should be defined")
      }
  }
}
