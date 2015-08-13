package io.strongtyped.active.slick.shapeless

import io.strongtyped.active.slick.Lens
import shapeless._
import scala.language.implicitConversions

object ShapelessSlick {

  /**
   * Converts a Shapeless lens to ActiveSlick's Lens
   */
  implicit def shapelessToSimple[O, V](lens: shapeless.Lens[O, V]): Lens[O, V] = {
    Lens(
      obj => lens.get(obj),
      (obj, value) => lens.set(obj)(value)
    )
  }

}