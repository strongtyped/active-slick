package io.strongtyped.active.slick


import slick.ast.BaseTypedType

import scala.language.existentials

trait Schema extends JdbcProfileProvider {

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


  class SupplierDao extends EntityActions(jdbcProfile) with OptimisticLocking {

    import jdbcProfile.api._

    val baseTypedType: BaseTypedType[Id] = implicitly[BaseTypedType[Id]]

    type Entity = Supplier
    type EntityTable = SuppliersTable

    class SuppliersTable(tag: Tag) extends Table[Supplier](tag, "SUPPLIERS") {

      def version = column[Long]("VERSION")

      def name = column[String]("SUPPLIER_NAME")

      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

      def * = (name, version, id.?) <>(Supplier.tupled, Supplier.unapply)

    }

    val tableQuery = TableQuery[EntityTable]

    def $id(table: EntityTable) = table.id

    val idLens = Lens[Supplier, Option[Int]](_.id, (supp, id) => supp.copy(id = id))

    def $version(table: EntityTable) = table.version

    val versionLens = Lens[Supplier, Long](_.version, (supp, version) => supp.copy(version = version))

    def createSchema = {
      import jdbcProfile.api._
      tableQuery.schema.create
    }
  }


  val Suppliers = new SupplierDao

  implicit class SupplierRecord(val entity: Supplier) extends ActiveRecord[Supplier] {

    val crudActions: CrudActions = Suppliers
  }

  class BeersDao extends EntityActions(jdbcProfile) {

    import jdbcProfile.api._

    val baseTypedType: BaseTypedType[Id] = implicitly[BaseTypedType[Id]]

    type Entity = Beer
    type EntityTable = BeersTable

    // Beer Table, DAO and Record extension
    class BeersTable(tag: Tag) extends Table[Beer](tag, "BEERS") {

      def name = column[String]("BEER_NAME")

      def supID = column[Int]("SUP_ID")

      def price = column[Double]("PRICE")

      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

      def * = (name, supID, price, id.?) <>(Beer.tupled, Beer.unapply)

      def supplier = foreignKey("SUP_FK", supID, Suppliers.tableQuery)(_.id)
    }

    val tableQuery = TableQuery[EntityTable]

    def $id(table: EntityTable) = table.id

    val idLens = Lens[Beer, Option[Int]](_.id, (beer, id) => beer.copy(id = id))

    def createSchema = {
      import jdbcProfile.api._
      tableQuery.schema.create
    }
  }

  val Beers = new BeersDao

  implicit class BeerRecord(val entity: Beer) extends ActiveRecord[Beer] {

    val crudActions: CrudActions = Beers

    def supplier() = Suppliers.findOptionById(entity.supID)
  }

}