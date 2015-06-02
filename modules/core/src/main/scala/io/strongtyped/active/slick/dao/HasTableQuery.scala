package io.strongtyped.active.slick.dao

import io.strongtyped.active.slick.{Identifiable, TableQueries, Tables}

trait HasTableQuery[M <: Identifiable, T <: Tables#EntityTable[M]] {

  def tableQuery: TableQueries#EntityTableQuery[M, T]
}
