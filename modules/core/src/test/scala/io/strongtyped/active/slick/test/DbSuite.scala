package io.strongtyped.active.slick.test

import io.strongtyped.active.slick.JdbcProfileProvider
import org.scalatest._

import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{Await, ExecutionContext}
import scala.language.postfixOps
import scala.util.{Failure, Success}

trait DbSuite extends BeforeAndAfterAll with Matchers with OptionValues with TryValues  {

  self:Suite with JdbcProfileProvider =>

  import jdbcProfile.api._

  def setupDb : jdbcProfile.backend.DatabaseDef

  private lazy val database:jdbcProfile.backend.DatabaseDef = setupDb

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

    val markedForRollback = dbAction.flatMap { result =>
      DBIO
        .failed(RollbackException(result))
        .map(_ => result) // map back to T
    }.transactionally.asTry

    val finalAction =
      markedForRollback.map {
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