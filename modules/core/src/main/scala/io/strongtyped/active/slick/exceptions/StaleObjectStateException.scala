package io.strongtyped.active.slick.exceptions

import io.strongtyped.active.slick.Identifiable

case class StaleObjectStateException[T <: Identifiable](staleObject: T, current: T)
  extends ActiveSlickException(s"Optimistic locking error - object in stale state: $staleObject, current in DB: $current")