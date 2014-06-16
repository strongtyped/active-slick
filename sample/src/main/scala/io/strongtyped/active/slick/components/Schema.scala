package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.{Tables, Profile}
import io.strongtyped.active.slick.models.{Beer, Supplier}

trait Schema { this:Tables with Profile =>

  import jdbcDriver.simple._

  class SuppliersTable(tag: Tag) extends IdentifiableTable[Supplier, Int](tag, "SUPPLIERS") {

    def name = column[String]("SUPPLIER_NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <> (Supplier.tupled, Supplier.unapply)
  }

  val Suppliers = TableQuery[SuppliersTable]


  class BeersTable(tag: Tag) extends IdentifiableTable[Beer, Int](tag, "BEERS") {

    def name = column[String]("BEER_NAME")
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, supID, price, id.?) <> (Beer.tupled, Beer.unapply)

    def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
  }
  val Beers = TableQuery[BeersTable]


  def createSchema(implicit sess:Session) = {
    (Suppliers.ddl ++ Beers.ddl).create
  }
}
