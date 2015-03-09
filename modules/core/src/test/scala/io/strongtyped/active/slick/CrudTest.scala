package io.strongtyped.active.slick

import io.strongtyped.active.slick.models.Entry
import io.strongtyped.active.slick.test.DbSuite
import shapeless._
import slick.dbio._

class CrudTest extends DbSuite {

//  implicit val cake = new  ActiveTestCake {
//
//    import driver.api._
//
//    class EntryTable(tag: Tag) extends EntityTable[Entry](tag, "ENTRIES_CRUD") {
//      def name = column[String]("NAME")
//      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
//      def * = (name, id.?) <>(Entry.tupled, Entry.unapply)
//    }
//
//    object Entries extends EntityTableQuery[Entry, EntryTable](
//      cons = tag => new EntryTable(tag),
//      idLens = lens[Entry] >> 'id
//    )
//
//    def createSchema(implicit sess: Session): Unit = Entries.ddl.create
//
//    implicit class EntryExtensions(val model: Entry) extends ActiveRecord[Entry]  {
//      override def table = Entries
//    }
//  }
//
//  import cake._
//
//  "A CRUD model" should "support all CRUD operations" in {
//    db { implicit sess =>
//
//      val initialCount = Entries.count
//
//      // test add
//      val savedEntry = Entry("Foo").save
//
//      savedEntry.id shouldBe 'defined
//
//      Entries.count shouldBe (initialCount + 1)
//
//      // test update
//      savedEntry.copy(name = "bar").save
//
//      val id = savedEntry.id.get
//
//      Entries.findById(id).name shouldBe "bar"
//
//      savedEntry.delete
//
//      Entries.count shouldBe initialCount
//    }
//  }

  def setupSchema: DBIO[Unit] = ???
}
