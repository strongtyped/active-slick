package io.strongtyped.active.slick

import slick.driver.PostgresDriver

trait PostgresProfileProvider extends JdbcProfileProvider {
  type JP = PostgresDriver
  val jdbcProfile = PostgresDriver
}