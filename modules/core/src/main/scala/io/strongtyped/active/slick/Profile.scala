package io.strongtyped.active.slick

import slick.driver.JdbcProfile

trait Profile {
  protected val profile:JdbcProfile
}
