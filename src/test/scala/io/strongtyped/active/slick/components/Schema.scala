package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.{Tables, Profile}
import io.strongtyped.active.slick.models.{Coffee, Supplier}

trait Schema { this:Tables with Profile =>

  import jdbcDriver.simple._

  class SuppliersTable(tag: Tag) extends IdentifiableTable[Supplier, Int](tag, "SUPPLIERS") {

    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, street, city, state, zip, id.?) <> (Supplier.tupled, Supplier.unapply)
  }

  val Suppliers = TableQuery[SuppliersTable]


  class CoffeesTable(tag: Tag) extends IdentifiableTable[Coffee, Int](tag, "COFFEES") {

    def name = column[String]("COF_NAME")
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, supID, price, id.?) <> (Coffee.tupled, Coffee.unapply)

    def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
  }
  val Coffees = TableQuery[CoffeesTable]


  def createSchema(implicit sess:Session) = (Suppliers.ddl ++ Coffees.ddl).create
}
