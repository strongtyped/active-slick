package io.strongtyped.active.slick.components

import io.strongtyped.active.slick._
import io.strongtyped.active.slick.models.{Beer, Supplier}
import shapeless._
import slick.util.Logging

trait Schema extends Logging with QueryCapabilities {
  this: Tables with EntityTableQueries with TableQueries with Profile =>

  import driver.api._

  class SuppliersTable(tag: Tag) extends VersionableEntityTable[Supplier](tag, "SUPPLIERS") {

    def version = column[Long]("VERSION")

    def name = column[String]("SUPPLIER_NAME")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, version, id.?) <>(Supplier.tupled, Supplier.unapply)

  }

  val Suppliers = new VersionableEntityTableQuery[Supplier, SuppliersTable](
    // construct function
    cons = tag => new SuppliersTable(tag),
    // shapeless lens to assign and extract id
    idLens = lens[Supplier] >> 'id,
    // shapeless lens to assign and extract version
    versionLens = lens[Supplier] >> 'version
  )
  // with FetchAll


  class BeersTable(tag: Tag) extends EntityTable[Beer](tag, "BEERS") {

    def name = column[String]("BEER_NAME")

    def supID = column[Int]("SUP_ID")

    def price = column[Double]("PRICE")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, supID, price, id.?) <>(Beer.tupled, Beer.unapply)

    def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
  }

  val idFunc = (beer: Beer) => {
    beer.id
  }

  val Beers = EntityTableQuery[Beer, BeersTable](
    // construct function
    cons = tag => new BeersTable(tag),
    // shapeless lens to assign and extract id
    idLens = lens[Beer] >> 'id
  )

  def create: DBIO[Unit] = {
    logger.info("Creating schema ... ")
    (Suppliers.schema ++ Beers.schema).create
  }

}