package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.{Beer, Supplier}
import scala.slick.jdbc.JdbcBackend

trait SupplierExtensions {
  this: ActiveSlick with ModelExtensions =>

  import jdbcDriver.simple._

  implicit class SupplierQueryExt(supplierQuery: TableQuery[SuppliersTable])
    extends IdentifiableTableExt[Supplier, Int](supplierQuery) {

    def extractId(supplier: Supplier)(implicit sess: Session): Option[Int] =
      supplier.id

    def withId(supplier: Supplier, id: Int)(implicit sess: Session) =
      supplier.copy(id = Some(id))

    protected [components] def queryMostExpensiveBeer(supplierId: Option[Int]) =
      Beers.queryMostExpensiveBeerBySupplier.filter(_.supID === LiteralColumn(supplierId))

  }


  implicit class SupplierExtensions(supplier:Supplier) {

    def save(implicit session: JdbcBackend#Session): Supplier = Suppliers.save(supplier)
    def delete(implicit session: JdbcBackend#Session): Boolean = Suppliers.delete(supplier)
    def add(implicit session: JdbcBackend#Session) : Int = Suppliers.add(supplier)

    def mostExpensiveBeer(implicit session: JdbcBackend#Session) : Option[Beer] = {
        Suppliers.queryMostExpensiveBeer(supplier.id).firstOption
    }
  }

}
