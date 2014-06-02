package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.{Supplier, Coffee}
import scala.slick.jdbc.JdbcBackend


trait CoffeeExtensions  {
  this: ActiveSlick with ModelExtensions =>

  import jdbcDriver.simple._

  implicit class CoffeeQueryExt(coffeeQuery:TableQuery[CoffeesTable])
    extends EntityTableExt[Coffee](coffeeQuery)


  implicit class CoffeeExtensions(coffee:Coffee) {

    def save(implicit session: JdbcBackend#Session): Coffee = Coffees.save(coffee)
    def delete(implicit session: JdbcBackend#Session): Boolean = Coffees.delete(coffee)

    def supplier(implicit session: JdbcBackend#Session) : Option[Supplier] = {
      coffee.id.flatMap(Suppliers.findOptionById)
    }
  }

}
