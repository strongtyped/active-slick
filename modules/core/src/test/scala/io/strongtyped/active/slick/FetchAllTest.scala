package io.strongtyped.active.slick

import io.strongtyped.active.slick.models.Entry
import io.strongtyped.active.slick.test.DbSuite
import shapeless._

class FetchAllTest extends DbSuite {

  implicit val cake = new  ActiveTestCake with QueryCapabilities {

    import jdbcDriver.simple._

    class EntryTable(tag: Tag) extends EntityTable[Entry](tag, "ENTRIES_FETCH_ALL") {
      def name = column[String]("NAME")
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def * = (name, id.?) <>(Entry.tupled, Entry.unapply)
    }

    object Entries extends EntityTableQuery[Entry, EntryTable](
      cons = tag => new EntryTable(tag),
      idLens = lens[Entry] >> 'id
    ) with FetchAll

    def createSchema(implicit sess: Session): Unit = Entries.ddl.create

    implicit class EntryExtensions(val model: Entry) extends ActiveRecord[Entry]  {
      override def table = Entries
    }
  }


  import cake._

  "A TableQuery with FetchAll" should "be able to fetch all entities" in {
    db { implicit sess =>

      val initialCount = Entries.count

      // test add
      Entry("Foo").save
      Entry("Bar").save

      Entries.fetchAll should have size(initialCount + 2)
    }
  }


}
