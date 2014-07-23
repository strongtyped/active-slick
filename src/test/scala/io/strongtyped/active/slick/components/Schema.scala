package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.{TableQueries, Tables, Profile}
import io.strongtyped.active.slick.models.{Beer, Supplier}

import scala.slick.util.Logging

trait Schema extends Logging { this: Tables with TableQueries with Profile =>

  import jdbcDriver.simple._

  class SuppliersTable(tag: Tag) extends IdVersionTable[Supplier, Int](tag, "SUPPLIERS")  {

    def version = column[Long]("VERSION")
    def name = column[String]("SUPPLIER_NAME")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, version, id.?) <> (Supplier.tupled, Supplier.unapply)

  }

  val Suppliers = new VersionableTableQuery[Supplier, SuppliersTable](tag => new SuppliersTable(tag)) {}


  class BeersTable(tag: Tag) extends IdTable[Beer, Int](tag, "BEERS") {

    def name = column[String]("BEER_NAME")
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, supID, price, id.?) <> (Beer.tupled, Beer.unapply)

    def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
  }
  val Beers = new IdentifiableTableQuery[Beer, BeersTable](tag => new BeersTable(tag)) {}



  def createSchema(implicit sess:Session) = {
    logger.info("Creating schema ... ")
    (Suppliers.ddl ++ Beers.ddl).create
  }

}
