package io.strongtyped.active.slick.models

case class Beer(name: String,
                supID: Int,
                price: Double,
                id: Option[Int] = None) extends Identifiable {
  type Id = Int
}

