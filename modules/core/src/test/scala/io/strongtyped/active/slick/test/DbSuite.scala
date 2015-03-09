package io.strongtyped.active.slick.test

import org.scalatest._
import slick.driver.{H2Driver, JdbcActionComponent}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scala.concurrent.duration._

trait DbSuite extends FlatSpec with Matchers with OptionValues with TryValues
  with JdbcActionComponent with H2Driver with BeforeAndAfterAll {

  import slick.driver.H2Driver.api._

  def query[T](dbAction: DBIO[T])(implicit timeout:FiniteDuration = 5 seconds): T =
    runAction(dbAction)

  def commit[T](dbAction: DBIO[T])(implicit timeout:FiniteDuration = 5 seconds): T =
    runAction(dbAction.transactionally)

  def rollback[T](dbAction: DBIO[T])(implicit ex:ExecutionContext, timeout:FiniteDuration = 5 seconds): T =
    runAction(Rollback.flatMap (_ =>  dbAction ))

  /** defaults to rollback */
  def runOnDb[T](dbAction: DBIO[T])(implicit ex:ExecutionContext, timeout:FiniteDuration = 5 seconds): T =
    rollback(dbAction)


  private def runAction[T](dbAction: DBIO[T])(implicit timeout:FiniteDuration): T = {

    val dbUrl = s"jdbc:h2:mem:active-slick-${this.getClass.getSimpleName}"
    val db = Database.forURL(dbUrl, driver = "org.h2.Driver")
    db.createSession().force() // keep the database in memory with an extra connection

    try {
      val result = db.run(dbAction)
      Await.result(result, 5 seconds)
    } finally {
      db.close()
    }
  }

  def setupSchema: DBIO[Unit]

  override protected def beforeAll(): Unit = {
    commit {
      setupSchema
    }
  }
}
