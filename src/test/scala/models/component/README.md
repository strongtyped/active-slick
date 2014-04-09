# Component based example

In this example, the DAO is defined inside a Component trait.

A case class Person is mapped to a DB column. The JdbcProfile is not defined and will be provided when the trait is mixed in.
All necessary slick implicit conversions are imported directly from the profile.

See [PersonDaoWithComponent.scala](PersonDaoWithComponent.scala)

Inside the PersonDao we define the Table mapping, the TableQuery and the methods extractId(person:Person),  withId(person:Person, id:Long) : Person and queryById(id: I): Query[Table[M], M].

[PersonDaoInComponentTest.scala](PersonDaoInComponentTest.scala) demonstrate the usage.

The DAO is initialized inside a withSession block. All DB operations are managed by the DAO.

Note that in this example, the slick session is passed to the DAO constructor as an implicit parameter.
Therefore, the  DAO can't be an Object. This is the recommend way of initializing a DAO.

The DAO lives inside a withSession or withTransaction block and is attached to the current session. As such we avoid polluting the DAO methods with an extra parameter block for an implicit session.
