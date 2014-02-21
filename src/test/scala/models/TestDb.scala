package models

import scala.slick.driver.H2Driver.simple._

object TestDb {
  val testDb = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver")
}
