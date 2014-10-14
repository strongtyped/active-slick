# ActiveSlick

ActiveSlick is a tiny library that offers CRUD operations for Slick 2.0 projects. The main goal is to provide some basic operations to manage the lifecycle of persisted objects (new/persisted/deleted/stale).

All operations are provided by `TableQueries` sub-classes. 


[![Build Status](https://travis-ci.org/strongtyped/active-slick.svg?branch=develop)](https://travis-ci.org/strongtyped/active-slick)

### Main features
- Basic CRUD and auxiliary methods - add/update/save, delete, list, pagedList, (paged result), findById and count.
- Model classes don't need to implement any specific class or trait,
  although for convenience you can extend `Identifiable` or `Versionable` traits.  
- Generic Id type. 
- `Identifiable` trait and respective `IdTableExt` to manage Entities.
- `Versionable` trait and respective `VersionableTableExt` for optimistic locking.


### Motivation

Slick is able to map result sets to case classes or Tuples because of its isomorphism. This is done thanks to built-in Scala features. However, there is no direct link between case class fields and database columns. Everything is done based on isomorphic projections.

As a consequence, managing of Entities IDs must be done by hand, over and over again. One needs to save a model, ask Slick to return the generated ID and add it explicitly to the case class.  The example bellow demonstrates how it is typically done in a Slick application.  

```scala
case class Foo(name:String, id:Option[Int] = None)

class FooTable(tag:Tag) extends Table[Foo](tag, "FOOS") {
    def name = column[String]("NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, id.?) <> (Foo.tupled, Foo.unapply)
}
val Foos = TableQuery[FooTable]

val foo = Foo("foo")
val id = Foos.returning(Foos.map(_.id)).insert(foo)
foo.copy(id = Some(id))
```

Both `Foo` and `FooTable` have an **ID** representing the primary key. On `Foo` is a field of type `Option[Int]` and on `FooTable` is a method returning a `Column[Int]`, but they are not **linked** to each other. In order to provide some generic functionality to manage Entities both **IDs** must be connected.

### Mapping using ActiveSlick
The following code fragment illustrates how this can be done using ActiveSlick's `IdTable` and a `TableWithIdQuery` extension based on `ActiveTableQuery`.

```scala

trait MappingWithActiveSlick {
  this: ActiveSlick =>

  import jdbcDriver.simple._

  case class Foo(name: String, id: Option[Int] = None) 

  class FooTable(tag: Tag) extends IdTable[Foo, Int](tag, "FOOS") {
    def name = column[String]("NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, id.?) <>(Foo.tupled, Foo.unapply)
  }

  val Foos = new TableWithIdQuery[Foo, Int, FooTable](tag => new FooTable(tag)) {
    override def extractId(model: Foo)(implicit sess: Session) = model.id
    override def withId(model: Foo, id: Int)(implicit sess: Session) = model.copy(id = Some(id))
  }
}

```

The mapping is almost the same. The only difference being that `FooTable` extends `IdTable` which requires an extra type parameter for the primary key. `IdTable` will also require us to define the method `def id:Column[I]` (`I` being the type of our ID, obviously). 

> Note the usage of the Cake Pattern. The Cake Pattern is used here to provide a `JdbcDriver` so we can have access to all implicit type classes and conversions needed to build the CRUD queries. This is required by ActiveSlick as all tables and table query extensions needs a `JdbcDriver`.

We have now a well known column for our primary key. The next step is to define the method to extract the id from our model and to add a generated id back into the model. 


To use the above mapping on our code we'll need to define a `Component` that will get all necessary traits mixed in and the `JdbcDriver` of our choice. 

```scala
object MappingWithActiveSlickApp {

  class Components(override val jdbcDriver: JdbcDriver) extends ActiveSlick with MappingWithActiveSlick {
    import jdbcDriver.simple._
    val db = Database.forURL("jdbc:h2:mem:active-slick", driver = "org.h2.Driver")
    def createSchema(implicit sess:Session) = Foos.ddl.create
  }
  object Components { val instance = new Components(H2Driver) }
  import Components.instance._

  def main(args:Array[String]) : Unit = {
    db.withTransaction { implicit sess =>
      createSchema
      val foo = Foo("foo")
      val fooWithId : Foo = Foos.save(foo)
      assert(fooWithId.id.isDefined, "Foo's ID should be defined")
    }
  }
}
``` 

The `save` method will take care of all the necessary boiler plate for inserting or updating a new record and returning a clone of `Foo` with an assigned ID (in case of an insert). 


### Mapping using ActiveSlick's Identifiable trait

The previous example can be further improved if our model extends the `Identifiable` trait. When using the `Identifiable` trait we only have to implement the `withId` method on the model class. And the `FooTable` must extend `IdTable` instead.  

```scala

trait MappingActiveSlickIdentifiable {
  this: ActiveSlick =>

  import jdbcDriver.simple._

  case class Foo(name: String, id: Option[Int] = None) extends Identifiable[Foo] {
    override type Id = Int
    override def withId(id: Id): Foo = copy(id = Some(id))
  }

  class FooTable(tag: Tag) extends EntityTable[Foo](tag, "FOOS") {
    def name = column[String]("NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, id.?) <> (Foo.tupled, Foo.unapply)
  }

  val Foos = new EntityTableQuery[Foo, FooTable](tag => new FooTable(tag))
}

```
Note that the `Identifiable` trait is parametrized on the type that will extend it, in this case `Foo`. As such we can have a self-type reference and guarantee that `withId` will return the same type. 
(see [Identifiable](https://github.com/strongtyped/active-slick/blob/develop/modules/core/src/main/scala/io/strongtyped/active/slick/models/Identifiable.scala) implementation)

Moreover, it defines a type alias `Id` instead of a type parameter. This type alias will be used in a type projection by `EntityTableQuery`. We can now let the compiler check that the id of the model matches the type parameter of the table's id column. 
(see [EntityTable](https://github.com/strongtyped/active-slick/blob/develop/modules/core/src/main/scala/io/strongtyped/active/slick/Tables.scala#L55) and [EntityTableQuery](https://github.com/strongtyped/active-slick/blob/develop/modules/core/src/main/scala/io/strongtyped/active/slick/TableQueries.scala#L131)implementations)

### TODO
- more testing, examples and docs
- macro to generate some helper methods like: findByName, findByBirthday, etc.
