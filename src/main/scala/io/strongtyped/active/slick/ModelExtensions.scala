package io.strongtyped.active.slick

trait ModelExtensions { this: ActiveSlick with Tables =>

  import jdbcDriver.simple._

  type Model[U] = RichModel[U, _]
  abstract class RichModel[U, T <: BaseIdTableExt[U, _]](val model: U, val table: T) {
    def query: TableQuery[_ <: Table[U]] = table.query
    def save()(implicit session: Session) = table.save(model)
  }
}
