package io.strongtyped.active.slick.models

trait Versionable[E <: Versionable[E]] {
  // self-typing to E to force withId to return this type
  self: E =>

  type Version

  def version: Option[E#Version]

  def withVersion(id: E#Version): E
}

