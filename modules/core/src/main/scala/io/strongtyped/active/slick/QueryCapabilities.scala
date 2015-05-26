package io.strongtyped.active.slick


trait QueryCapabilities {
  this: Profile =>

  import driver.api._

  trait DeleteAll { this: TableQuery[_ <: Table[_]] =>
    def deleteAll():  DBIO[Int] = {
      this.filter(_ => LiteralColumn(true)).delete
    }
  }

}