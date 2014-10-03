package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.{Supplier, Beer}
import scala.slick.jdbc.JdbcBackend
import scala.util.Try


trait BeerExtensions  {
  this: ActiveSlick with ModelExtensions =>

  implicit class BeerExtensions(coffee:Beer) {

    def save(implicit session: JdbcBackend#Session): Beer = Beers.save(coffee)
    def trySave(implicit session: JdbcBackend#Session): Try[Beer] = Beers.trySave(coffee)

    def delete(implicit session: JdbcBackend#Session): Boolean = Beers.delete(coffee)
    def tryDelete(implicit session: JdbcBackend#Session): Try[Boolean] = Beers.tryDelete(coffee)

    def supplier(implicit session: JdbcBackend#Session) : Option[Supplier] =
      Suppliers.findOptionById(coffee.supID)
  }

}
