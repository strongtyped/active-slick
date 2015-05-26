package io.strongtyped.active.slick.models

/**
 * Base trait to define a model having an ID (i.e.: Entity).
 * The ID is defined as a type alias as it needs to
 * be accessed by ActiveSlick via type projection when mapping to database tables.
 */
trait Identifiable {

  /** The type of this Entity ID */
  type Id
}