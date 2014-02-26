package models.component

import scala.slick.driver.JdbcProfile

trait BaseComponent {

  val profile:JdbcProfile
  import profile.simple._

}
