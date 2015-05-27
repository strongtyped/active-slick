package io.strongtyped.active.slick


trait Capabilities {
  this: Profile =>

  import profile.api._

  trait DeleteAll { this: TableQuery[_ <: Table[_]] =>
    def deleteAll():  DBIO[Int] = {
      this.filter(_ => LiteralColumn(true)).delete
    }
  }

}