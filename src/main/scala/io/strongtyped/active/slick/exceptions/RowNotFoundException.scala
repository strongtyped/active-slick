package io.strongtyped.active.slick.exceptions



class RowNotFoundException[T](notFoundRecord:T)
  extends ActiveSlickException(s"Record not found: $notFoundRecord")