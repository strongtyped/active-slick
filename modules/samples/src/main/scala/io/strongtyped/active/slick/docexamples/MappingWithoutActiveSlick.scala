package io.strongtyped.active.slick.docexamples

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

object MappingWithoutActiveSlick {

  import slick.driver.H2Driver.api._

  case class Foo(name: String, id: Option[Int] = None)

  class FooTable(tag: Tag) extends Table[Foo](tag, "FOOS") {
    def name = column[String]("NAME")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <> (Foo.tupled, Foo.unapply)
  }

  val Foos = TableQuery[FooTable]

  val db = Database.forURL("jdbc:h2:mem:active-slick", driver = "org.h2.Driver")
  try {

    Await.ready(db.run(Foos.schema.create), 200 millis)
    val foo = Foo("foo")
    val insertAction = db.run(Foos.returning(Foos.map(_.id)) += foo)
    val id = Await.result(insertAction, 200 millis)
    foo.copy(id = Some(id))

  } finally {
    db.close()
  }

}
