package io.strongtyped.active.slick.exceptions

case class ManyRowsAffectedException(affectedRecordsCount: Int)
  extends ActiveSlickException(s"Expected single row affected, got $affectedRecordsCount instead")