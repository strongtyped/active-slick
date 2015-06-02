package io.strongtyped.active.slick.test

import io.strongtyped.active.slick.{TableQueries, JdbcProfileProvider}
import org.scalatest.Suite
import slick.driver.{H2Driver, JdbcDriver}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

trait H2Suite extends DbSuite with JdbcProfileProvider { self:Suite =>

  val jdbcProfile: JdbcDriver = H2Driver

  import jdbcProfile.api._

  def createSchema: DBIO[Unit]

  def timeout = 5 seconds

  override def setupDb: jdbcProfile.backend.DatabaseDef = {

    val dbUrl = s"jdbc:h2:mem:${this.getClass.getSimpleName}"
    val db = Database.forURL(dbUrl, driver = "org.h2.Driver")
    db.createSession().force()

    val result = db.run(createSchema)
    Await.result(result, timeout)

    db
  }
}
