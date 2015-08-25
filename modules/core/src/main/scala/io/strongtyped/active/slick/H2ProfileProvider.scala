package io.strongtyped.active.slick

import slick.driver.H2Driver

trait H2ProfileProvider extends JdbcProfileProvider {
  type JP = H2Driver
  val jdbcProfile: H2Driver = H2Driver
}