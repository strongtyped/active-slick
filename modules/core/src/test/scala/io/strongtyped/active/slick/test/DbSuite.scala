package io.strongtyped.active.slick.test

import io.strongtyped.active.slick.{Profile, ActiveRecordExtensions, ActiveSlick}
import org.scalatest._

import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{Await, ExecutionContext}
import scala.language.postfixOps
import scala.util.{Failure, Success}

trait DbSuite extends BeforeAndAfterAll with Matchers with ActiveSlick with ActiveRecordExtensions  {

  self:Suite with Profile =>

  import driver.api._

  def setupDb : driver.backend.DatabaseDef

  private lazy val database:driver.backend.DatabaseDef = setupDb

  override protected def afterAll(): Unit = {
    database.close()
  }

  // keep the database in memory with an extra connection

  def query[T](dbAction: DBIO[T])(implicit ex: ExecutionContext, timeout: FiniteDuration = 5 seconds): T =
    runAction(dbAction)

  def commit[T](dbAction: DBIO[T])(implicit ex: ExecutionContext, timeout: FiniteDuration = 5 seconds): T =
    runAction(dbAction.transactionally)

  def rollback[T](dbAction: DBIO[T])(implicit ex: ExecutionContext, timeout: FiniteDuration = 5 seconds): T = {

    case class RollbackException(expected: T) extends RuntimeException("rollback exception")

    val rollbackAction = dbAction.flatMap { result =>
      // NOTE:
      // DBIO.failed returns DBIOAction[Nothing, NoStream, Effect], but we need to preserve T
      // otherwise, we'll end up with a 'R' returned by 'transactionally' method
      // this seems to be need when compiling for 2.10.x (probably a bug fixed on 2.11.x series)
      DBIO.failed(RollbackException(result)).map(_ => result) // map back to T
    }.transactionally.asTry

    val finalAction =
      rollbackAction.map {
        case Success(result)                    => result
        case Failure(RollbackException(result)) => result
        case Failure(other)                     => throw other
      }

    runAction(finalAction)
  }

  private def runAction[T](dbAction: DBIO[T])(implicit ex: ExecutionContext, timeout: FiniteDuration): T = {
    val result = database.run(dbAction)
    Await.result(result, timeout)
  }

}