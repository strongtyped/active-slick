package io.strongtyped.active.slick.shapeless

import io.strongtyped.active.slick.SimpleLens
import shapeless._
import scala.language.implicitConversions

object ShapelessSlick {

  /**
    * Converts a Shapeless lens to ActiveSlick's SimpleLens 
    */
  implicit def shapelessToSimple[O, V](lens: shapeless.Lens[O, V]) : SimpleLens[O, V] = {
    SimpleLens(
      obj => lens.get(obj),
      (obj, value) => lens.set(obj)(value)
    )
  }

}