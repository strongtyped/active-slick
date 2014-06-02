package io.strongtyped.active.slick

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{OptionValues, Matchers, FunSpec}
import io.strongtyped.active.slick.components.Components

trait DbTest extends FunSpec with Matchers with OptionValues {

  import Components.instance._
  val db = Database.forURL("jdbc:h2:mem:hello", driver = "org.h2.Driver")

  def DB = new {
    def apply[T](block: Session => T) : T = {
      db.withTransaction { implicit session =>
        createSchema
        block(session)
      }

    }
  }
}
