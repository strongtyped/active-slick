package io.strongtyped.active.slick.exceptions

import io.strongtyped.active.slick.Identifiable

case class StaleObjectStateException[T <: Identifiable](staleObject: T)
  extends ActiveSlickException(s"Optimistic locking error - object in stale state: $staleObject")