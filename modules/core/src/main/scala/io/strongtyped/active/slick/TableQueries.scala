package io.strongtyped.active.slick

import slick.ast.BaseTypedType

import scala.language.implicitConversions

trait TableQueries extends Tables { self: JdbcProfileProvider =>

  import jdbcProfile.api._


  class TableWithIdQuery[M, I, T <: Tables#IdTable[M, I]](cons: Tag => T)
                                                         (implicit ev:BaseTypedType[I])
    extends TableQuery(cons) {

    def count: DBIO[Int] = this.size.result
  }

  class EntityTableQuery[M <: Identifiable, T <: Tables#EntityTable[M]](cons: Tag => T)
                                                                       (implicit ev: BaseTypedType[M#Id])
    extends TableWithIdQuery[M, M#Id, T](cons)


  object EntityTableQuery {
    def apply[M <: Identifiable, T <: Tables#EntityTable[M]](cons: Tag => T)(implicit ev: BaseTypedType[T#Model#Id]) = {
      new EntityTableQuery[T#Model, T](cons)
    }
  }

}
