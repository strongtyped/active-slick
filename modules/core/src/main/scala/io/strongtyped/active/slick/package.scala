package io.strongtyped.active

package object slick {

  type H2ProfileProvider = JdbcProfileProvider.H2ProfileProvider
  type PostgresProfileProvider = JdbcProfileProvider.PostgresProfileProvider
  type DerbyProfileProvider = JdbcProfileProvider.DerbyProfileProvider
  type HsqlProfileProvider = JdbcProfileProvider.HsqlProfileProvider
  type MySQLProfileProvider = JdbcProfileProvider.MySQLProfileProvider
  type SQLLiteProfileProvider = JdbcProfileProvider.SQLLiteProfileProvider
}
