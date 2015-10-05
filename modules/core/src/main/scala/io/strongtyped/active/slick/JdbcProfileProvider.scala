package io.strongtyped.active.slick

import slick.driver.{DerbyDriver, SQLiteDriver, MySQLDriver, HsqldbDriver, PostgresDriver, H2Driver, JdbcProfile}


trait JdbcProfileProvider {
  type JP <: JdbcProfile
  val jdbcProfile: JP
}

object JdbcProfileProvider {

  trait H2ProfileProvider extends JdbcProfileProvider {
    type JP = H2Driver
    val jdbcProfile: H2Driver = H2Driver
  }

  trait PostgresProfileProvider extends JdbcProfileProvider {
    type JP = PostgresDriver
    val jdbcProfile = PostgresDriver
  }


  trait DerbyProfileProvider extends JdbcProfileProvider {
    type JP = DerbyDriver
    val jdbcProfile = DerbyDriver
  }

  trait HsqlProfileProvider extends JdbcProfileProvider {
    type JP = HsqldbDriver
    val jdbcProfile = HsqldbDriver
  }

  trait MySQLProfileProvider extends JdbcProfileProvider {
    type JP = MySQLDriver
    val jdbcProfile = MySQLDriver
  }

  trait SQLLiteProfileProvider extends JdbcProfileProvider {
    type JP = SQLiteDriver
    val jdbcProfile = SQLiteDriver
  }

}