package io.strongtyped.active.slick.docexamples

import io.strongtyped.active.slick.{ActiveRecord, JdbcProfileProvider, Lens, EntityActions}
import io.strongtyped.active.slick.docexamples.codegen.Tables
import slick.ast.BaseTypedType

/**
 * Shows how to configure active-slick with a schema that was generated via slick-codegen. In this case,
 * A schema is generated on build as the object io.strongtyped.active.slick.docexamples.codegen.Tables.
 * See codegen_schema.sql for the schema that feeds into the codegen.
 */
object ActiveSlickWithCodegen {
  object ComputersRepo extends EntityActions with JdbcProfileProvider {
    //
    // Implement JdbcProfileProvider with JDBCProfile from generated Tables.scala
    //
    override type JP = Tables.profile.type // Sucks that this is necessary. Did we have to define this type in JdbcProfileProvider? Why not just use JdbcProfile?
    override val jdbcProfile = Tables.profile

    //
    // Implement EntityActions
    //
    import jdbcProfile.api._

    type Entity = Tables.ComputersRow
    type Id = Long
    type EntityTable = Tables.Computers

    val baseTypedType = implicitly[BaseTypedType[Id]]
    val tableQuery = Tables.Computers
    val idLens: Lens[Tables.ComputersRow, Option[Long]] = {
      // For the getter, use 0L as a sentinel value because generated ID is usually non-optional
      Lens.lens { row: Tables.ComputersRow => if (row.id == 0L) None else Some(row.id) }
                { (row, maybeId) => maybeId map { id => row.copy(id = id) } getOrElse row }
    }

    override def $id(table: EntityTable): Rep[Long] = {
      table.id
    }

    implicit class EntryExtensions(val model: Tables.ComputersRow) extends ActiveRecord(ComputersRepo)
  }
}
