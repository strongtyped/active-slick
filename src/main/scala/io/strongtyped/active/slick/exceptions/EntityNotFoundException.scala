package io.strongtyped.active.slick.exceptions

class EntityNotFoundException[T](notFoundRecord:T)
  extends ActiveSlickException(s"Record not found: $notFoundRecord")