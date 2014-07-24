package io.strongtyped.active.slick

trait ModelExtensions { this: ActiveSlick with Tables =>

  import jdbcDriver.simple._

  abstract class Model[U](val query: TableQuery[_ <: Table[U]])
}
