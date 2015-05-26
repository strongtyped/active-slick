package io.strongtyped.active.slick

import slick.driver.JdbcDriver

trait Profile {
  val driver: JdbcDriver
}
