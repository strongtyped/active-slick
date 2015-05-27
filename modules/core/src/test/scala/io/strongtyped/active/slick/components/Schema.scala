package io.strongtyped.active.slick.components

import io.strongtyped.active.slick._
import io.strongtyped.active.slick.models.{Beer, Supplier}
import slick.util.Logging

trait Schema extends Logging with Capabilities {
  this: Tables with EntityTableQueries with TableQueries with Profile =>

  import profile.api._

  class SuppliersTable(tag: Tag) extends VersionableEntityTable[Supplier](tag, "SUPPLIERS") {

    def version = column[Long]("VERSION")

    def name = column[String]("SUPPLIER_NAME")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, version, id.?) <> (Supplier.tupled, Supplier.unapply)

  }

  val Suppliers = new VersionableEntityTableQuery[Supplier, SuppliersTable](
    // construct function
    cons = tag => new SuppliersTable(tag),
    // lens to assign and extract id
    idLens = SimpleLens[Supplier, Option[Int]](_.id, (supplier, id) => supplier.copy(id = id)),
    // lens to assign and extract version
    versionLens = SimpleLens[Supplier, Long](_.version, (supplier, version) => supplier.copy(version = version))
  )


  class BeersTable(tag: Tag) extends EntityTable[Beer](tag, "BEERS") {

    def name = column[String]("BEER_NAME")

    def supID = column[Int]("SUP_ID")

    def price = column[Double]("PRICE")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, supID, price, id.?) <> (Beer.tupled, Beer.unapply)

    def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
  }

  val idFunc = (beer: Beer) => {
    beer.id
  }

  val Beers = EntityTableQuery[Beer, BeersTable](
    // construct function
    cons = tag => new BeersTable(tag),
    // lens to assign and extract id
    idLens = SimpleLens[Beer, Option[Int]](_.id, (beer, id) => beer.copy(id = id))
  )

  def create: DBIO[Unit] = {
    logger.info("Creating schema ... ")
    (Suppliers.schema ++ Beers.schema).create
  }

}