# Simple usage of slick-dao

In this example, we have a very simple usage of the generic slick-dao.

A case class Person is mapped to a DB column. The jdbc driver is hardcoded and all necessary slick implicit conversions are imported directly from the hardcoded driver.

See [Person.scala](Person.scala)

Inside the PersonDao we define the Table mapping, the TableQuery and the methods extractId(person:Person),  withId(person:Person, id:Long) : Person and queryById(id: I): Query[Table[M], M].

[PersonDaoTest.scala](PersonDaoTest.scala) demonstrate the usage.

The DAO is initialized inside a withSession block. All DB operations are managed by the DAO.

Note that in this example, the slick session is passed to the DAO constructor as an implicit parameter.
Therefore, the  DAO can't be an Object. This is the recommend way of initializing a DAO, IMO.

The DAO lives inside a withSession or withTransaction block and is attached to the current session. As such we avoid polluting the DAO methods with an extra parameter block for an implicit session.
