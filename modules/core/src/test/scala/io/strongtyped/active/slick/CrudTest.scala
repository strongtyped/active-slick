package io.strongtyped.active.slick

import io.strongtyped.active.slick.dao.EntityDao
import io.strongtyped.active.slick.test.H2Suite
import org.scalatest.FlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class CrudTest extends FlatSpec with H2Suite with TableQueries with JdbcProfileProvider {

  import jdbcProfile.api._

  behavior of "An EntityDao (CRUD)"

  it should "support all CRUD operations" in {
    rollback {
      for {
        // collect initial count
        initialCount <- entryDao.count

        // save new entry
        savedEntry <- Entry("Foo").save()

        // count again, must be initialCount + 1
        count <- entryDao.count

        // update entry
        updatedEntry <- savedEntry.copy(name = "Bar").save()

        // find it back from DB
        found <- entryDao.findById(savedEntry.id.get)

        // delete it
        _ <- found.delete()

        // count total one more time
        finalCount <- entryDao.count
      } yield {

        // check that we can add new entry
        count shouldBe (initialCount + 1)

        // check entity properties
        savedEntry.id shouldBe 'defined
        savedEntry.name shouldBe "Foo"

        // found entry must be a 'Bar'
        found.name shouldBe "Bar"

        // after delete finalCount must equal initialCount
        finalCount shouldBe initialCount

        savedEntry
      }
    }
  }


  override def createSchema = EntryTableQuery.schema.create


  case class Entry(name: String, id: Option[Int] = None) extends Identifiable {
    type Id = Int
  }

  class EntryTable(tag: Tag) extends EntityTable[Entry](tag, "ENTRIES_CRUD_TEST") {

    def name = column[String]("NAME")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <>(Entry.tupled, Entry.unapply)
  }

  val EntryTableQuery = EntityTableQuery[Entry, EntryTable](tag => new EntryTable(tag))

  class EntryDao extends EntityDao[Entry, EntryTable](jdbcProfile) {
    val tableQuery = EntryTableQuery
    val idLens = SimpleLens[Entry, Option[Int]](_.id, (entry, id) => entry.copy(id = id))
  }

  val entryDao = new EntryDao


  implicit class EntryExtensions(val model: Entry) extends ActiveRecord[Entry] {
    val dao = entryDao
  }

}
