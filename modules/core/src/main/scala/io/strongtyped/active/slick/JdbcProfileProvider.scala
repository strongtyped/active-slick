package io.strongtyped.active.slick

import slick.driver.JdbcProfile

trait JdbcProfileProvider {
  type JP <: JdbcProfile
  val jdbcProfile: JP
}
