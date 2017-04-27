import sbt.Keys._
import sbt._

object Dependencies {
  val slickVersion = "3.2.0"
  val slick = "com.typesafe.slick" %% "slick" % slickVersion

  val shapelessDeps = Seq("com.chuusai" %% "shapeless" % "2.3.2")
  /*Def setting (
    CrossVersion partialVersion scalaVersion.value match {
      case Some((2, scalaMajor)) if scalaMajor >= 11 =>
        Seq("com.chuusai" %% "shapeless" % "2.3.1")
      case Some((2, 10)) =>
        Seq(
          "com.chuusai" % "shapeless" % "2.1.0" cross CrossVersion.full,
          compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
        )
    }
  )*/


  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  val h2database = "com.h2database" % "h2" % "1.4.181" % "test"

  val mainDeps = Seq(
    libraryDependencies ++= Seq(slick)
  )

  val testDeps = Seq(
    libraryDependencies ++= Seq(h2database, scalaTest)
  )
}