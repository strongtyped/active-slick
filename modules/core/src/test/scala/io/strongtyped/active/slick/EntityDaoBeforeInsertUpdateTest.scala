package io.strongtyped.active.slick

import io.strongtyped.active.slick.dao.EntityDao
import io.strongtyped.active.slick.test.H2Suite
import org.scalatest.FlatSpec

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class EntityDaoBeforeInsertUpdateTest extends FlatSpec with H2Suite with TableQueries with JdbcProfileProvider {

  import jdbcProfile.api._

  behavior of "An EntityDao with validation "

  it should "return an error if beforeInsert is not successful" in {

    val result =
      rollback {
        for {
          result <- Entry("  ").save().asTry
        } yield result
      }

    result.failure.exception shouldBe a[NameShouldNotBeEmptyException]
  }

  it should "return an error if beforeUpdate is not successful" in {

    val result =
      rollback {

        val finalAction =
          for {
            savedEntry <- Entry("abc").save()
            // update name should fail according to beforeUpdate method definition
            updatedEntry <- savedEntry.copy(name = "Bar").save()
          } yield updatedEntry

        finalAction.asTry
      }

    result.failure.exception shouldBe a[NameCanNotBeModifiedException]
  }


  override def createSchema = EntryTableQuery.schema.create


  case class Entry(name: String, id: Option[Int] = None) extends Identifiable {
    type Id = Int
  }

  class EntryTable(tag: Tag) extends EntityTable[Entry](tag, "ENTRIES_VALIDATION_TEST") {

    def name = column[String]("NAME")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <>(Entry.tupled, Entry.unapply)

  }

  val EntryTableQuery = EntityTableQuery[Entry, EntryTable](tag => new EntryTable(tag))


  class NameShouldNotBeEmptyException extends RuntimeException("Name should not be empty")
  class NameCanNotBeModifiedException extends RuntimeException("Name can not be modified")

  class EntryDao extends EntityDao[Entry, EntryTable](jdbcProfile) {
    val tableQuery = EntryTableQuery
    val idLens = SimpleLens[Entry, Option[Int]](_.id, (entry, id) => entry.copy(id = id))

    override def beforeInsert(model: Entry)(implicit exc: ExecutionContext): DBIO[Entry] = {
      if (model.name.trim.isEmpty) {
        DBIO.failed(new NameShouldNotBeEmptyException)
      } else {
        DBIO.successful(model)
      }
    }

    override def beforeUpdate(id: Int, model: Entry)(implicit exc: ExecutionContext):  DBIO[Entry] = {
      findById(id).flatMap { oldModel =>
        if (oldModel.name != model.name) {
          DBIO.failed(new NameCanNotBeModifiedException)
        } else {
          DBIO.successful(model)
        }
      }
    }
  }

  val entryDao = new EntryDao


  implicit class EntryExtensions(val model: Entry) extends ActiveRecord[Entry] {
    val dao = entryDao
  }

}