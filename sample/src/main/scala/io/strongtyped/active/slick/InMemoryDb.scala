package io.strongtyped.active.slick

import io.strongtyped.active.slick.components.Components
import scala.slick.driver.H2Driver.simple._

object InMemoryDb {

  import io.strongtyped.active.slick.components.Components.instance._

  def DB = new {
    def apply[T](block: Session => T) : T = {
      val db = Database.forURL("jdbc:h2:mem:hello", driver = "org.h2.Driver")
      db.withTransaction { implicit session =>
        createSchema
        block(session)
      }
    }
  }
}
