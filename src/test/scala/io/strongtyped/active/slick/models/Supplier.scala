package io.strongtyped.active.slick.models

case class Supplier(name: String,
                    street: String,
                    city: String,
                    state: String,
                    zip: String,
                    id: Option[Int] = None)
