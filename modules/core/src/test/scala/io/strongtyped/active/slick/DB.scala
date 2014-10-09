package io.strongtyped.active.slick

import io.strongtyped.active.slick.components.Components.instance._

import scala.slick.driver.H2Driver
import scala.slick.driver.H2Driver.simple._

sealed trait TxOps {
  def complete(sess: Session): Unit
}

object Rollback extends TxOps {
  override def complete(sess: Session): Unit = sess.rollback()
}

object Commit extends TxOps {
  override def complete(sess: Session): Unit = ()
}

object DB {

  lazy val db = {
    val db = Database.forURL("jdbc:h2:mem:active-slick", driver = "org.h2.Driver")
    val keepAliveSession = db.createSession()
    keepAliveSession.force() // keep the database in memory with an extra connection
    db.withTransaction { implicit session =>
      createSchema
    }
    db
  }

  def commit[T](block: Session => T): T = apply(Commit)(block)
  def rollback[T](block: Session => T): T = apply(Rollback)(block)

  private def apply[T](block: Session => T): T = apply(Rollback)(block)

  def apply[T](txOps: TxOps)(block: Session => T): T = {
    db.withTransaction { implicit session =>
      val result = block(session)
      txOps.complete(session)
      result
    }
  }

}
