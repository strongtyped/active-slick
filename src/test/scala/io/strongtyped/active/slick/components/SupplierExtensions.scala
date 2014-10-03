package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.Supplier
import scala.slick.jdbc.JdbcBackend
import scala.util.Try

trait SupplierExtensions {
  this: ActiveSlick with ModelExtensions =>

  implicit class SupplierExtensions(val model:Supplier) extends ActiveRecord[Supplier] {
    override def table = Suppliers
  }

}
