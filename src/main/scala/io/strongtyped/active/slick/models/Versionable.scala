package io.strongtyped.active.slick.models

trait Versionable[E <: Versionable[E]] {

  def version: Long

  def withVersion(id: Long): E
}

