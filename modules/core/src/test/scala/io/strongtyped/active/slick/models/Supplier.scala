package io.strongtyped.active.slick.models

case class Supplier(name: String, version: Long = 0, id: Option[Int] = None) extends Identifiable {
  override type Id = Int
}
