package io.strongtyped.active.slick

/** A dead simple lens to provide a getter and a setter for 
  * lifecycle model/table fields like 'id' and 'version'.
  *
  * This implementation is by no means target as a general Lens library. It's sole purpose is
  * to provide an abstraction for setting and getting first level fields on an arbitrary model.
  *
  * @param get function mapping O -> V 
  * where O is the Object we want to 'look' inside and V the value of the field we want to extract
  *
  * @param set function to assign a the a new value to field affect by this Lens.
  */
case class Lens[O, V](get: O => V, set: (O, V) => O)

object Lens {

  /** 
    * Convenience method to allow creation of a Lens using curried syntax.
    * {{{
    *   case class Coffee(id:Int, name:String)
    *
    *   val idLens = lens { coffee: Coffee => coffee.id  }
    *                     { (coffee, id) => coffee.copy(id = id) } 
    * }}}
    */
  def lens[O, V](get: O => V)(set: (O, V) => O) = Lens(get, set)
}