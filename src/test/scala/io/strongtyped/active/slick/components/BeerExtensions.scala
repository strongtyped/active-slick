package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.{Supplier, Beer}
import scala.slick.jdbc.JdbcBackend
import scala.util.Try


trait BeerExtensions  {
  this: ActiveSlick with ModelExtensions =>

  implicit class BeerExtensions(val model:Beer) extends ActiveRecord[Beer, BeersTable] {

    override val table = Beers

    def supplier(implicit session: JdbcBackend#Session) : Option[Supplier] =
      Suppliers.findOptionById(model.supID)
  }

}
