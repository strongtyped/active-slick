package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.Supplier
import scala.slick.jdbc.JdbcBackend

trait SupplierExtensions {
  this: ActiveSlick with ModelExtensions =>

  implicit class SupplierExtensions(supplier:Supplier) {
    def save(implicit session: JdbcBackend#Session): Supplier = Suppliers.save(supplier)
    def delete(implicit session: JdbcBackend#Session): Boolean = Suppliers.delete(supplier)
  }

}
