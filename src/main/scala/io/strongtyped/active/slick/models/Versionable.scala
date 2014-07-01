package io.strongtyped.active.slick.models

trait Versionable[E <: Versionable[E]] {
  // self-typing to E to force withId to return this type
  self: E =>

  def version: Long

  def withVersion(id: Long): E
}

