# slick-dao

Generic DAO for Slick is a tiny library that offers CRUD operations for Slick 2.0 mapped classes.

### Main features
- basic CRUD and auxiliary methods - add/update/save, delete, pages (paged result), findById and count
- Model classes don't need to implement any specific class or trait. No base Entity trait whatsoever. 
- Generic Id type
- ActiveRecord implicit conversion


It currently requires you to adopt the Cake Pattern in order to get a Jdbc driver injected at compile time into your DAO. 

(example from test)
```scala
case class Person(firstName: String, lastName: String, id: Option[Int] = None)

trait PersonComponent extends BaseDaoComponent {

  import profile.simple._

  object PersonDao extends SlickJdbcDao[Person, Int] {

    def query = TableQuery[Persons]

    class Persons(tag: Tag) extends profile.simple.Table[Person](tag, "person") with IdentifiableTable[Int] {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def firstName = column[String]("first_name")
      def lastName = column[String]("last_name")
      def * = (firstName, lastName, id.?) <> (Person.tupled, Person.unapply)
    }


    def extractId(row: Person): Option[Int] =
      row.id

    def withId(row: Person, id: Int): Person =
      row.copy(id = Option(id))
  }

  implicit class PersonExtensions(val person:Person) extends PersonDao.ActiveRecord(person)
}
```

The PersonComponent must be mixed in with a Component providing a JdbcProfile based on your driver of choice. Once this initial setup is done, you can access your DAO methods as follow:

```scala
val person = PersonDao.findById(1)
PersonDao.count
PersonDao.save(person)
```
If PersonExtensions is on scope, Person object gets enhanced with an ActiveRecord methods.
```scala
val person = PersonDao.findById(1)
person.copy(name = "John").save // save method provided by ActiveRecord implicit conversion
```



### TODO

- testing, some examples and docs
- remove Cake Pattern. Although very nice and my prefered way of doing it, it should be optional. 
- a Versionable trait to reinforce versioning
- investigate how we can have DAO for rows with compoung keys instead of PKs. Should be possible by defining key as a tuple and mapping it as such
- error handling strategy - what if entity or record doesn’t exist. Exception? Either? ScalaUtils.Or? 
- macro to generate some helper methods like: findByName, findByBirthday, etc. 