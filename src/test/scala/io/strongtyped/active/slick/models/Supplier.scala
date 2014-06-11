package io.strongtyped.active.slick.models

case class Supplier(name: String,
                    street: String,
                    city: String,
                    state: String,
                    zip: String,
                    id: Option[Int] = None) extends Entity[Supplier] {

  type Id = Int
  override def withId(id: Id) = copy(id = Option(id))
}
