package io.strongtyped.active.slick

trait ActiveSlick
  extends Tables with TableQueries with EntityTableQueries
  with ActiveRecordExtensions with Profile
