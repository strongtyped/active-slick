package io.strongtyped.active.slick

import slick.driver.JdbcProfile

trait JdbcProfileProvider {
  val jdbcProfile:JdbcProfile
}
