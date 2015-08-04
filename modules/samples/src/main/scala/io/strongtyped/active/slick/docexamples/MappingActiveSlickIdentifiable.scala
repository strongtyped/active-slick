package io.strongtyped.active.slick.docexamples

import _root_.io.strongtyped.active.slick._
import slick.driver.H2Driver

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps


object MappingActiveSlickIdentifiable extends  JdbcProfileProvider {

  val jdbcProfile = H2Driver
  import jdbcProfile.api._

  case class Foo(name: String, id: Option[Int] = None) extends Identifiable {
    override type Id = Int
  }

  object FooActions$$$ extends EntityActions[Foo](jdbcProfile) {

    class FooTable(tag: Tag) extends jdbcProfile.api.Table[Foo](tag, "FOOS") {
      def name = column[String]("NAME")
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def * = (name, id.?) <>(Foo.tupled, Foo.unapply)
    }
    type EntityTable = FooTable


    def $id(table:FooTable) = table.id

    val tableQuery = TableQuery[FooTable]

    val idLens = SimpleLens[Foo, Option[Int]](_.id, (foo, id) => foo.copy(id = id))


    def createSchema = {
      import jdbcProfile.api._
      tableQuery.schema.create
    }
  }

  def run[T](block: Database => T): T = {
    val db = Database.forURL("jdbc:h2:mem:active-slick", driver = "org.h2.Driver")
    try {
      Await.ready(db.run(FooActions$$$.createSchema), 200 millis)
      block(db)
    } finally {
      db.close()
    }
  }

  def main(args: Array[String]): Unit = {
    run { db =>
      val foo = Foo("foo")
      val fooWithId = Await.result(db.run(FooActions$$$.save(foo)), 200 millis)
      assert(fooWithId.id.isDefined, "Foo's ID should be defined")
    }
  }
}
