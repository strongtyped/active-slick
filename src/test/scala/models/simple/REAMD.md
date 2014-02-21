# Simple usage of slick-dao

In this example, we have a very minimal usage of the generic slick-dao.

A case class Person is mapped to a DB column. The jdbc driver is hardcoded and all necessary slick implicit conversions are import directly from the hardcoded driver.

Inside the PersonDao we define the Table mapping, the TableQuery and the methods extractId(row:R) and withId(row:R, id:I) : R.

PersonTest demonstrate the usage.
The DAO is initialized inside a withSession block. All DB operations are managed by the DAO.

