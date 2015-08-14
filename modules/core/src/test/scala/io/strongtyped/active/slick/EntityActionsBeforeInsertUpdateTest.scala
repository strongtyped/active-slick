package io.strongtyped.active.slick

import io.strongtyped.active.slick.test.H2Suite
import org.scalatest.FlatSpec
import slick.ast.BaseTypedType

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global


class EntityActionsBeforeInsertUpdateTest
  extends FlatSpec with H2Suite with JdbcProfileProvider {

  behavior of "An EntityDao with validation "

  it should "return an error if beforeInsert is not successful" in {

    val result =
      rollback {
        for {
          result <- Foo("  ").save().asTry
        } yield result
      }

    result.failure.exception shouldBe a[NameShouldNotBeEmptyException]
  }

  it should "return an error if beforeUpdate is not successful" in {

    val result =
      rollback {

        val finalAction =
          for {
            savedEntry <- Foo("abc").save()
            // update name should fail according to beforeUpdate method definition
            updatedEntry <- savedEntry.copy(name = "Bar").save()
          } yield updatedEntry

        finalAction.asTry
      }

    result.failure.exception shouldBe a[NameCanNotBeModifiedException]
  }


  override def createSchemaAction = {
    foos.createSchema
  }


  case class Foo(name: String, id: Option[Int] = None) extends Identifiable {

    type Id = Int
  }


  class NameShouldNotBeEmptyException extends RuntimeException("Name should not be empty")

  class NameCanNotBeModifiedException extends RuntimeException("Name can not be modified")

  class FooDao extends EntityActions(jdbcProfile) {

    import jdbcProfile.api._

    val baseTypedType: BaseTypedType[Id] = implicitly[BaseTypedType[Id]]

    type EntityTable = FooTable
    type Entity = Foo

    class FooTable(tag: Tag) extends Table[Foo](tag, "FOO_VALIDATION_TEST") {

      def name = column[String]("NAME")

      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

      def * = (name, id.?) <>(Foo.tupled, Foo.unapply)

    }

    val tableQuery = TableQuery[FooTable]

    def $id(table: FooTable) = table.id

    val idLens = Lens[Foo, Option[Int]](_.id, (entry, id) => entry.copy(id = id))

    //@formatter:off
    // tag::adoc[]
    override def beforeInsert(model: Foo)
                             (implicit exc: ExecutionContext): DBIO[Foo] = {
      if (model.name.trim.isEmpty) {
        DBIO.failed(new NameShouldNotBeEmptyException)
      } else {
        DBIO.successful(model)
      }
    }

    override def beforeUpdate(id: Int, model: Foo)
                             (implicit exc: ExecutionContext): DBIO[Foo] = {
      findById(id).flatMap { oldModel =>
        if (oldModel.name != model.name) {
          DBIO.failed(new NameCanNotBeModifiedException)
        } else {
          DBIO.successful(model)
        }
      }
    }
    // end::adoc[]
    //@formatter:on

    def createSchema = {
      import jdbcProfile.api._
      tableQuery.schema.create
    }
  }

  val foos = new FooDao


  implicit class EntryExtensions(val entity: Foo) extends ActiveRecord[Foo] {

    val crudActions = foos
  }

}
