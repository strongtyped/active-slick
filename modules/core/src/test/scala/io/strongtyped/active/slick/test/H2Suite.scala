package io.strongtyped.active.slick.test

import io.strongtyped.active.slick.{H2ProfileProvider, JdbcProfileProvider}
import org.scalatest.Suite
import slick.driver.{H2Driver, JdbcDriver}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

trait H2Suite extends DbSuite with H2ProfileProvider {
  self: Suite =>

  import jdbcProfile.api._

  def createSchemaAction: DBIO[Unit] = DBIO.successful(())

  def timeout = 5 seconds

  override def setupDb: jdbcProfile.backend.DatabaseDef = {
    // each test suite gets its own isolated DB
    val dbUrl = s"jdbc:h2:mem:${this.getClass.getSimpleName};DB_CLOSE_DELAY=-1"
    val db = Database.forURL(dbUrl, driver = "org.h2.Driver")
    val result = db.run(createSchemaAction.transactionally)
    Await.result(result, timeout)
    db
  }
}
