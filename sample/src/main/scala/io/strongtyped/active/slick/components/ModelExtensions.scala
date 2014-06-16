package io.strongtyped.active.slick.components

import io.strongtyped.active.slick.ActiveSlick

trait ModelExtensions extends Schema
                              with SupplierExtensions
                              with BeerExtensions {
     this: ActiveSlick =>
}
