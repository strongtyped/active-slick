package io.strongtyped.active.slick

trait SchemaManagement {
  self: EntityActions =>

  import jdbcProfile.api._

  def createSchema: DBIO[Unit] = tableQuery.schema.create

  def dropSchema: DBIO[Unit] = tableQuery.schema.drop
}
