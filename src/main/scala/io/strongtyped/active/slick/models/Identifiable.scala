package io.strongtyped.active.slick.models

trait Identifiable[E <: Identifiable[E]] {

  type Id

  def id: Option[E#Id]

  def withId(id: E#Id): E
}

