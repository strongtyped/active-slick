package io.strongtyped.active.slick

trait Tables {  this:Profile =>

  import jdbcDriver.simple._

  trait TableWithId[I] {
    def id: Column[I]
  }


  abstract class IdTable[M, I](tag: Tag, schemaName: Option[String], tableName: String)
                                        (implicit val colType: BaseColumnType[I])
    extends Table[M](tag, schemaName, tableName) with TableWithId[I] {

    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[I]) = this(tag, None, tableName)
  }



  trait TableWithVersion  {
    def version: Column[Long]
  }
//
//  abstract class VersionTable[M](tag: Tag, schemaName: Option[String], tableName: String)
//    extends Table[M](tag, schemaName, tableName) with TableWithVersion {
//
//    def this(tag: Tag, tableName: String) = this(tag, None, tableName)
//  }

  abstract class IdVersionTable[M, I](tag: Tag, schemaName: Option[String], tableName: String)(implicit val colType: BaseColumnType[I])
    extends Table[M](tag, schemaName, tableName) with TableWithId[I] with TableWithVersion {

    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[I]) = this(tag, None, tableName)
  }
}
