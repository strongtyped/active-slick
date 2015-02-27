package io.strongtyped.active.slick.models

case class  Entry(name: String, id: Option[Int] = None) extends Identifiable {
  type Id = Int
}