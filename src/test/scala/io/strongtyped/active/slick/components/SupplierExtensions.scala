package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.Supplier
import scala.slick.jdbc.JdbcBackend

trait SupplierExtensions {
  this: ActiveSlick with ModelExtensions =>

  import jdbcDriver.simple._

  implicit class SupplierQueryExt(supplierQuery:TableQuery[SuppliersTable])
    extends VersionableTableExt[Supplier](supplierQuery)

  implicit class SupplierExtensions(supplier:Supplier) {
    def save(implicit session: JdbcBackend#Session): Supplier = Suppliers.save(supplier)
    def delete(implicit session: JdbcBackend#Session): Boolean = Suppliers.delete(supplier)
  }

}
