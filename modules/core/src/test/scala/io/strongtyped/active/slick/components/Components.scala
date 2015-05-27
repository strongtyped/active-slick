package io.strongtyped.active.slick.components

import slick.driver.{ JdbcDriver, H2Driver, JdbcProfile }
import io.strongtyped.active.slick.ActiveSlick

class Components(override val profile: JdbcDriver)
  extends ActiveSlick with ModelExtensions

object Components {
  val instance = new Components(H2Driver)
}
