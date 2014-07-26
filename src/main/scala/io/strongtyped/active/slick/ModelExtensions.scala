package io.strongtyped.active.slick

import io.strongtyped.active.slick.models._

trait ModelExtensions { this: ActiveSlick with Tables =>

  import jdbcDriver.simple._

  type Model[U] = RichModel[U, _]
  abstract class RichModel[U, T <: BaseIdTableExt[U, _]](val model: U, val table: T) {
    def query: TableQuery[_ <: Table[U]] = table.query
    def save()(implicit session: Session) = table.save(model)
  }

  class ModelImplicits[U <: Identifiable[U]]
  (tab: TableQuery[_ <: IdTable[U, U#Id]])
  (implicit bct: BaseColumnType[U#Id]) {
    implicit val query: TableQuery[_ <: Table[U]] = tab
    implicit class QueryExt(query: TableQuery[_ <: Table[U]]) extends IdTableExt[U](tab)
    implicit class ModelExt(model: U) extends RichModel[U, QueryExt](model, new QueryExt(tab))
  }
}

