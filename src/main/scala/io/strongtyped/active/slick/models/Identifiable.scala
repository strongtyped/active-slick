package io.strongtyped.active.slick.models

trait Identifiable[E <: Identifiable[E]] {
  // self-typing to E to force withId to return this type
  self: E =>

  type Id
  def id: Option[E#Id]

  def withId(id: E#Id): E
}

