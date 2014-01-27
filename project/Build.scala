import sbt._
import sbt.Keys._

object SlickDaoBuild extends Build {

  val buildName         = "SlickDao"
  val appVersion        = "0.0.1-SNAPSHOT"
  val scalaBuildOptions = Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls")


  val repos = Seq(
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Typesafe snapshots"  at "http://repo.typesafe.com/typesafe/snapshots/",
    "Sonatype snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots"
  )

  val BuildSettings = Project.defaultSettings ++ Seq(
    scalaVersion := "2.10.3",
    scalacOptions := scalaBuildOptions,
    resolvers ++= repos,
    version := appVersion
  )

  val mainDependencies = Seq(
    "com.typesafe.slick"    %%  "slick"                   % "2.0.0",
    "org.scalautils"        %%  "scalautils"              % "2.0",
    "org.slf4j"             %   "slf4j-nop"               % "1.6.4",
    "com.h2database"        %   "h2"                      % "1.3.166",

    "org.scalatest"         %%  "scalatest"               % "2.0"         % "test"
  )

  lazy val root = Project(
    id = "slick-dao",
    base = file("."),
    settings = BuildSettings ++ Seq(
      libraryDependencies ++= mainDependencies
    )
  )
}