package io.strongtyped.active.slick

/** A dead simple lens to provide a getter and a setter for 
  * lifecycle model/table fields like 'id' and 'version'.
  *
  * This implementation is by no means target as a general Lens library. It's sole purpose is
  * to provide an abstraction for setting and getting first level fields on an arbitrary model.
  */
case class Lens[O, V](get: O => V, set: (O, V) => O)

object Lens {

  def lens[O, V](get: O => V, set: (O, V) => O) = Lens(get, set)
}