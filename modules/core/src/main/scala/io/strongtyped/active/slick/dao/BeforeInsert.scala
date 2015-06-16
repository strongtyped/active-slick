package io.strongtyped.active.slick.dao

import slick.driver.JdbcProfile

trait BeforeInsert[M] {

  val jdbcProfile:JdbcProfile
  import jdbcProfile.api._

  def beforeInsert(model:M): DBIO[M]
}
