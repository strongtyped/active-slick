package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.{Supplier, Beer}
import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted


trait BeerExtensions  {
  this: ActiveSlick with ModelExtensions =>

  import jdbcDriver.simple._

  implicit class BeerQueryExt(beerQuery:TableQuery[BeersTable])
    extends EntityTableExt[Beer](beerQuery) {


    protected [components] def queryMaxPriceBySupplier =
      Beers.groupBy { _.supID }.map {
        case (id, b) => id -> b.map(_.price).max
      }

    protected [components] def queryMostExpensiveBeerBySupplier = {
        for {
          beer <- Beers
          (supId, maxPrice) <- queryMaxPriceBySupplier
            if beer.supID === supId && beer.price === maxPrice
        } yield beer
    }

    def mostExpensiveBeer(implicit session: JdbcBackend#Session) : Option[Beer] =
      queryMostExpensiveBeerBySupplier.firstOption

  }


  implicit class BeerExtensions(beer:Beer) {

    def save(implicit session: JdbcBackend#Session): Beer = Beers.save(beer)
    def delete(implicit session: JdbcBackend#Session): Boolean = Beers.delete(beer)

    def supplier(implicit session: JdbcBackend#Session) : Option[Supplier] =
      beer.id.flatMap(Suppliers.findOptionById)

  }

}
