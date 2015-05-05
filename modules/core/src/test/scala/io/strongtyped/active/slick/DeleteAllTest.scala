package io.strongtyped.active.slick

import io.strongtyped.active.slick.models.Identifiable
import io.strongtyped.active.slick.test.H2Suite
import org.scalatest.{FlatSpec, OptionValues}
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global


class DeleteAllTest extends FlatSpec with H2Suite with OptionValues with QueryCapabilities {

  import driver.api._

  behavior of "A DeleteAll capability mixin"


  it should "allow deletion of all entities" in {
    for {
    // initial count
    // collect initial count
      initialCount <- Entries.count

      // save foo
      foo <- Entry("Foo").save()

      // save bar
      bar <- Entry("Bar").save()
      // count again, must be initialCount + 2
      count <- Entries.count

      // update entry
      deleteCount <- Entries.deleteAll()

      // count total one more time
      finalCount <- Entries.count

    } yield {
      // check that we can add new entry
      count shouldBe (initialCount + 2)

      // delete count should equal total count
      deleteCount shouldBe count

      // after delete finalCount must equal initialCount
      finalCount shouldBe initialCount
    }
  }

  case class Entry(name: String, id: Option[Int] = None) extends Identifiable {

    type Id = Int
  }

  class EntryTable(tag: Tag) extends EntityTable[Entry](tag, "ENTRIES_DELETE_ALL") {

    def name = column[String]("NAME")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <>(Entry.tupled, Entry.unapply)
  }

  object Entries extends EntityTableQuery[Entry, EntryTable](
    cons = tag => new EntryTable(tag),
    idLens = SimpleLens[Entry, Option[Int]](_.id, (entry, id) => entry.copy(id = id))
  ) with DeleteAll


  implicit class EntryExtensions(val model: Entry) extends ActiveRecord[Entry] {

    override def tableQuery = Entries
  }

  override def createSchema = Entries.schema.create


}
