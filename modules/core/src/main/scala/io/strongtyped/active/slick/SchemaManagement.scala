package io.strongtyped.active.slick

import slick.dbio.Effect.Schema
import slick.profile.FixedSqlAction

trait SchemaManagement {
  self: EntityActions =>

  import jdbcProfile.api._

  def createSchema: DBIO[Unit] = tableQuery.schema.create

  def dropSchema: DBIO[Unit] = tableQuery.schema.drop
}
