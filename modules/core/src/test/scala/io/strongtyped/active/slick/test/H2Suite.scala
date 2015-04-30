package io.strongtyped.active.slick.test

import io.strongtyped.active.slick.Profile
import org.scalatest.Suite
import slick.driver.{H2Driver, JdbcDriver}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

trait H2Suite extends DbSuite {

  self:Suite with Profile =>

  val driver: JdbcDriver = H2Driver

  import driver.api._

  def createSchema: DBIO[Unit]

  def timeout = 5 seconds

  override def setupDb: driver.backend.DatabaseDef = {

    val dbUrl = s"jdbc:h2:mem:${this.getClass.getSimpleName}"
    val db = Database.forURL(dbUrl, driver = "org.h2.Driver")
    db.createSession().force()

    val result = db.run(createSchema)
    Await.result(result, timeout)

    db
  }
}
