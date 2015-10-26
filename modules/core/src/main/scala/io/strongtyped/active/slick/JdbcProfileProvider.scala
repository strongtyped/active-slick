package io.strongtyped.active.slick

import slick.driver.{DerbyDriver, SQLiteDriver, MySQLDriver, HsqldbDriver, PostgresDriver, H2Driver, JdbcProfile}


trait JdbcProfileProvider {
  val jdbcProfile: JdbcProfile
}

object JdbcProfileProvider {

  trait H2ProfileProvider extends JdbcProfileProvider {
    val jdbcProfile: H2Driver = H2Driver
  }

  trait PostgresProfileProvider extends JdbcProfileProvider {
    val jdbcProfile = PostgresDriver
  }

  trait DerbyProfileProvider extends JdbcProfileProvider {
    val jdbcProfile = DerbyDriver
  }

  trait HsqlProfileProvider extends JdbcProfileProvider {
    val jdbcProfile = HsqldbDriver
  }

  trait MySQLProfileProvider extends JdbcProfileProvider {
    val jdbcProfile = MySQLDriver
  }

  trait SQLLiteProfileProvider extends JdbcProfileProvider {
    val jdbcProfile = SQLiteDriver
  }

}