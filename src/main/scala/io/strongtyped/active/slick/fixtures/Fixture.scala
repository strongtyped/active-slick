package io.strongtyped.active.slick.fixtures

import io.strongtyped.active.slick._

trait Fixtures { this: ActiveSlick =>

  import jdbcDriver.simple._

  /**
   * Basic trait for database fixtures
   */
  abstract class Fixture[U : Model](val objects: Seq[U]) {
    val model = implicitly[Model[U]]

    def install()(implicit session: Session) = model.query.insertAll(objects : _*)
  }
}
