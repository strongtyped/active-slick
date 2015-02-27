package io.strongtyped.active.slick

import slick.driver.{H2Driver, JdbcDriver}

trait ActiveTestCake extends ActiveSlick with ActiveRecordExtensions {

  val jdbcDriver: JdbcDriver = H2Driver
  import jdbcDriver.simple._

  def createSchema(implicit sess: Session): Unit

}
