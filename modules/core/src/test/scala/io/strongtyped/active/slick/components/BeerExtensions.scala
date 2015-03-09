package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.{ Supplier, Beer }
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend
import scala.util.Try

trait BeerExtensions {
  this: ActiveSlick with ModelExtensions =>

  implicit class BeerExtensions(val model: Beer) extends ActiveRecord[Beer] {
    override val tableQuery = Beers
    def supplier(): DBIO[Option[Supplier]] = Suppliers.findOptionById(model.supID)
  }

}
