package io.strongtyped.active.slick

import io.strongtyped.active.slick.components.Components.instance._

import scala.slick.driver.H2Driver.simple._

object DB {

  val db = {
    val db = Database.forURL("jdbc:h2:mem:hello", driver = "org.h2.Driver")
    val keepAliveSession = db.createSession()
    keepAliveSession.force() // keep the database in memory with an extra connection
    db.withTransaction { implicit session =>
      createSchema
    }
    db
  }

  def apply[T](block: Session => T): T = {
    db.withTransaction { implicit session =>
      val result = block(session)
      session.rollback()
      result
    }
  }
}
