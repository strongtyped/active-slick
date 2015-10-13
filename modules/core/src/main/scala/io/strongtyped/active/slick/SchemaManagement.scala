package io.strongtyped.active.slick

trait SchemaManagement {
  self: EntityActions =>

  import jdbcProfile.api._

  def createSchema = tableQuery.schema.create

  def dropSchema = tableQuery.schema.drop
}
