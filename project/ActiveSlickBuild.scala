import sbt._
import sbt.Keys._

object ActiveSlickBuild extends Build {

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
    "org.slf4j"             %   "slf4j-nop"               % "1.6.4"
    

  )

  lazy val root = Project(
    id = "active-slick",
    base = file("."),
    settings = BuildSettings ++ Seq(
      libraryDependencies ++= mainDependencies
    )
  )

  lazy val sample = Project(
    id = "active-slick-sample",
    base = file("sample"),
    settings = BuildSettings ++ Seq(
      libraryDependencies ++= mainDependencies ++ Seq(
        "com.h2database"        %   "h2"                      % "1.3.166"
      )
    )
  ).dependsOn(root)
    .aggregate(root)
}