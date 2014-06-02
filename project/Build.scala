import sbt._
import sbt.Keys._

object SlickDaoBuild extends Build {

  val buildName         = "ActiveSlick"
  val appVersion        = "0.0.1-SNAPSHOT"
  val scalaBuildOptions = Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls")


  val BuildSettings = Project.defaultSettings ++ Seq(
    scalaVersion := "2.10.3",
    scalacOptions := scalaBuildOptions,
    version := appVersion
  )

  val mainDependencies = Seq(
    "com.typesafe.slick"    %%  "slick"                   % "2.0.0",
    "org.scalautils"        %%  "scalautils"              % "2.0",
    "org.slf4j"             %   "slf4j-nop"               % "1.6.4",
    

    "org.scalatest"         %%  "scalatest"               % "2.0"         % "test",
    "com.h2database"        %   "h2"                      % "1.3.166"     % "test"
  )

  lazy val root = Project(
    id = "active-slick",
    base = file("."),
    settings = BuildSettings ++ Seq(
      libraryDependencies ++= mainDependencies
    )
  )
}