package io.strongtyped.active.slick.components

import io.strongtyped.active.slick._
import io.strongtyped.active.slick.models.{ Beer, Supplier }
import shapeless._

import scala.slick.util.Logging

trait Schema extends Logging with QueryCapabilities {
  this: Tables
    with EntityTableQueries
    with TableQueries
    with Profile =>

  import jdbcDriver.simple._

  class SuppliersTable(tag: Tag) extends VersionableEntityTable[Supplier](tag, "SUPPLIERS") {

    def version = column[Long]("VERSION")
    def name    = column[String]("SUPPLIER_NAME")
    def id      = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, version, id.?) <> (Supplier.tupled, Supplier.unapply)

  }

  val Suppliers = new VersionableEntityTableQuery[Supplier, SuppliersTable](
    cons = tag => new SuppliersTable(tag),
    idLens = lens[Supplier] >> 'id,
    versionLens = lens[Supplier] >> 'version
  ) with FetchAll




  class BeersTable(tag: Tag) extends EntityTable[Beer](tag, "BEERS") {

    def name    = column[String]("BEER_NAME")
    def supID   = column[Int]("SUP_ID")
    def price   = column[Double]("PRICE")
    def id      = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, supID, price, id.?) <> (Beer.tupled, Beer.unapply)

    def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
  }

  val idFunc = (beer:Beer) => { beer.id }

  val Beers = EntityTableQuery[Beer, BeersTable](
    cons = tag => new BeersTable(tag),
    idLens = lens[Beer] >> 'id
  )

  def createSchema(implicit sess: Session) = {
    logger.info("Creating schema ... ")
    (Suppliers.ddl ++ Beers.ddl).create
  }

}