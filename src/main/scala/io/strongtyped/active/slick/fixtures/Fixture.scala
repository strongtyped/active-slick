package io.strongtyped.active.slick.fixtures

import io.strongtyped.active.slick._

trait Fixtures { this: ActiveSlick =>

  import jdbcDriver.simple._

  /**
   * Basic trait for database fixtures
   */
  abstract class Fixture[U](val objects: Seq[U])(implicit val table: TableQuery[_ <: Table[U]]) {
    def install()(implicit session: Session) = table.insertAll(objects : _*)
  }
}
