package io.strongtyped.active.slick.exceptions

case class StaleObjectStateException[T](staleObject: T)
  extends ActiveSlickException(s"Optimistic locking error - object in stale state: $staleObject")