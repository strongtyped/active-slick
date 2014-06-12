package io.strongtyped.active.slick

trait Tables {  this:Profile =>

  import jdbcDriver.simple._

  trait TableWithId[I] {
    def id: Column[I]
  }


  abstract class IdentifiableTable[M, I](tag: Tag, schemaName: Option[String], tableName: String)
                                        (implicit val colType: BaseColumnType[I])
    extends Table[M](tag, schemaName, tableName) with TableWithId[I] {

    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[I]) = this(tag, None, tableName)
  }



  trait TableWithVersion[I, V] extends TableWithId[I] {
    def version: Column[V]
  }

  abstract class IdVersionTable[M, I](tag: Tag, schemaName: Option[String], tableName: String)
                                     (implicit val colType: BaseColumnType[I])
    extends Table[M](tag, schemaName, tableName) with TableWithVersion[I, Long] {

    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[I]) = this(tag, None, tableName)
  }
}
