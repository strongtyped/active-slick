package models.component

import scala.slick.driver.{H2Driver, JdbcProfile}

class Components(override val profile: JdbcProfile)
  extends PersonComponent

object Components {
  val instance = new Components(H2Driver)
}
