package io.strongtyped.active.slick

import io.strongtyped.active.slick.dao.{EntityDao, OptimisticLocking, SlickDao}

import scala.language.existentials

trait Schema extends TableQueries with JdbcProfileProvider {

  import jdbcProfile.api._


  case class Supplier(name: String, version: Long = 0, id: Option[Int] = None) extends Identifiable {
    override type Id = Int
  }

  case class Beer(name: String,
                  supID: Int,
                  price: Double,
                  id: Option[Int] = None) extends Identifiable {
    type Id = Int
  }


  class SuppliersTable(tag: Tag) extends VersionableEntityTable[Supplier](tag, "SUPPLIERS") {

    def version = column[Long]("VERSION")

    def name = column[String]("SUPPLIER_NAME")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, version, id.?) <>(Supplier.tupled, Supplier.unapply)

  }

  val Suppliers = EntityTableQuery[Supplier, SuppliersTable](tag => new SuppliersTable(tag))

  class SupplierDao
    extends EntityDao[Supplier, SuppliersTable](jdbcProfile)
    with OptimisticLocking[Supplier, SuppliersTable] {

    val tableQuery = Suppliers

    val idLens = SimpleLens[Supplier, Option[Int]](_.id, (supp, id) => supp.copy(id = id))

    val versionLens =  SimpleLens[Supplier, Long](_.version, (supp, version) => supp.copy(version = version))
  }

  implicit class SupplierRecord(val model: Supplier) extends ActiveRecord[Supplier] {
    def dao: SlickDao[Supplier] = new SupplierDao
  }


  // Beer Table, DAO and Record extension
  class BeersTable(tag: Tag) extends EntityTable[Beer](tag, "BEERS") {

    def name = column[String]("BEER_NAME")

    def supID = column[Int]("SUP_ID")

    def price = column[Double]("PRICE")

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, supID, price, id.?) <>(Beer.tupled, Beer.unapply)

    def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
  }

  val Beers = EntityTableQuery[Beer, BeersTable](tag => new BeersTable(tag))

  class BeersDao extends EntityDao[Beer, BeersTable](jdbcProfile)  {

    val tableQuery = Beers

    val idLens = SimpleLens[Beer, Option[Int]](_.id, (beer, id) => beer.copy(id = id))
  }

  implicit class BeerRecord(val model:Beer) extends ActiveRecord[Beer] {

    val dao: SlickDao[Beer] = new BeersDao

    val supplierDao = new SupplierDao

    def supplier(): DBIO[Option[Supplier]] = supplierDao.findOptionById(model.supID)
  }

  def create: DBIO[Unit] = {
    (Suppliers.schema ++ Beers.schema).create
  }
}