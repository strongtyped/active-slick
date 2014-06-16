package io.strongtyped.active.slick

import io.strongtyped.active.slick.InMemoryDb._
import io.strongtyped.active.slick.models.{Beer, Supplier}
import io.strongtyped.active.slick.components.Components.instance._

import scala.slick.jdbc.JdbcBackend

object Example2  {
  def main(args:Array[String]) : Unit = {
    DB { implicit sess =>
      println("###################################")

      val supplier1 = createSuppliersWithBeer("Foo")
      val supplier2 = createSuppliersWithBeer("Bar", 1.23)

      showMostExpensiveBeer(supplier1)
      showMostExpensiveBeer(supplier2)

      println()
    }
  }

  def showMostExpensiveBeer(supplier:Supplier)(implicit sess: JdbcBackend#Session) {
    val priceString =
    supplier.mostExpensiveBeer.fold {
      "not prices available"
    } { beer =>
      beer.name + " - " + beer.price
    }
    println(s"Supp: ${supplier.name} - most expensive beer: $priceString" )
  }

  def createSuppliersWithBeer(name:String, factor: Double = 1)(implicit sess: JdbcBackend#Session) = {
    val sup = Supplier(name).save
      sup.id.map { supId =>
      (1 to 5).foreach { i =>
        Beer("Beer-" + i, supId, i * factor).save
      }
    }
    sup
  }
}
