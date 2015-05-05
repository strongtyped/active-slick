package io.strongtyped.active.slick

import io.strongtyped.active.slick.models.Identifiable
import io.strongtyped.active.slick.test.H2Suite
import org.scalatest.{FlatSpec, OptionValues}
import shapeless._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

class CrudTest extends FlatSpec with H2Suite with OptionValues {

  import driver.api._

  behavior of "A EntityTable (CRUD)"


  it should "support all CRUD operations" in {
    rollback {
      for {
        // collect initial count
        initialCount <- Entries.count

        // save new entry
        savedEntry <- Entry("Foo").save()

        // count again, must be initialCount + 1
        count <- Entries.count

        // update entry
        updatedEntry <- savedEntry.copy(name = "Bar").save()

        // find it back from DB
        found <- Entries.findById(savedEntry.id.get)

        // delete it
        _ <- found.delete()

        // count total one more time
        finalCount <- Entries.count
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


  override def createSchema = Entries.schema.create


  case class Entry(name: String, id: Option[Int] = None) extends Identifiable {
    type Id = Int
  }

  class EntryTable(tag: Tag) extends EntityTable[Entry](tag, "ENTRIES_CRUD") {

    def name = column[String]("NAME")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <>(Entry.tupled, Entry.unapply)
  }

  object Entries extends EntityTableQuery[Entry, EntryTable](
    cons = tag => new EntryTable(tag),
    idLens = SimpleLens[Entry, Option[Int]](_.id, (entry, id) => entry.copy(id = id))
  )


  implicit class EntryExtensions(val model: Entry) extends ActiveRecord[Entry] {

    override def tableQuery = Entries
  }

}
