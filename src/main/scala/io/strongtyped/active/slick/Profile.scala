package io.strongtyped.active.slick

import scala.slick.driver.JdbcDriver

trait Profile {
  val jdbcDriver: JdbcDriver
}
