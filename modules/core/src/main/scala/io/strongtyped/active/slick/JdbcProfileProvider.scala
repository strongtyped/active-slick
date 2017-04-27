package io.strongtyped.active.slick

import slick.jdbc.{DerbyProfile, SQLiteProfile, MySQLProfile, HsqldbProfile, PostgresProfile, H2Profile, JdbcProfile}


trait JdbcProfileProvider {
  type JP <: JdbcProfile
  val jdbcProfile: JP
}

object JdbcProfileProvider {

  trait H2ProfileProvider extends JdbcProfileProvider {
    override type JP = H2Profile
    override val jdbcProfile: H2Profile = H2Profile
  }

  trait PostgresProfileProvider extends JdbcProfileProvider {
    override type JP = PostgresProfile
    override val jdbcProfile: PostgresProfile = PostgresProfile
  }


  trait DerbyProfileProvider extends JdbcProfileProvider {
    override type JP = DerbyProfile
    override val jdbcProfile: DerbyProfile = DerbyProfile
  }

  trait HsqlProfileProvider extends JdbcProfileProvider {
    override type JP = HsqldbProfile
    override val jdbcProfile: HsqldbProfile = HsqldbProfile
  }

  trait MySQLProfileProvider extends JdbcProfileProvider {
    override type JP = MySQLProfile
    override val jdbcProfile: MySQLProfile = MySQLProfile
  }

  trait SQLLiteProfileProvider extends JdbcProfileProvider {
    override type JP = SQLiteProfile
    override val jdbcProfile: SQLiteProfile = SQLiteProfile
  }

}