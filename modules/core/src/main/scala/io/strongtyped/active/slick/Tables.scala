package io.strongtyped.active.slick

/**
 * Defines Slick table extensions.
 * To be mixed-in into a cake.
 */
trait Tables { self: JdbcProfileProvider =>

  import jdbcProfile.api._

  trait IdColumn[I] {

    def id: Rep[I]
  }

  trait VersionColumn {

    def version: Rep[Long]
  }

  /** Table extension to be used with a Model that has an Id. */
  abstract class IdTable[M, I](tag: Tag, schemaName: Option[String], tableName: String)(implicit val colType: BaseColumnType[I])
    extends Table[M](tag, schemaName, tableName) with IdColumn[I] {

    type Model = M
    type Id = I

    /** Constructor without schemaName */
    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[I]) = this(tag, None, tableName)

  }

  /** Table extension to be used with a Model that has an Id and version (optimistic locking). */
  abstract class IdVersionTable[M, I](tag: Tag, schemaName: Option[String], tableName: String)(override implicit val colType: BaseColumnType[I])
    extends IdTable[M, I](tag, schemaName, tableName)(colType) with VersionColumn {

    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[I]) = this(tag, None, tableName)
  }

  /**
   * Type alias for [[IdTable]]s mapping [[Identifiable]]s
   * Id type is mapped via type projection of Identifiable#Id
   */
  type EntityTable[M <: Identifiable] = IdTable[M, M#Id]

  /**
   * Type alias for [[IdTable]]s mapping [[Identifiable]]s with version.
   * Id type is mapped via type projection of Identifiable#Id
   */
  type VersionableEntityTable[M <: Identifiable] = IdVersionTable[M, M#Id]

}
