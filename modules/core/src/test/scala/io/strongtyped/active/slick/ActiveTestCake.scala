package io.strongtyped.active.slick

import slick.driver.{H2Driver, JdbcDriver}

trait ActiveTestCake extends ActiveSlick with ActiveRecordExtensions {

  val driver: JdbcDriver = H2Driver
  import driver.api._

  def createSchema(implicit sess: Session): Unit

}
