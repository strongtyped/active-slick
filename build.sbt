
name := "active-slick"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.h2database" % "h2" % "1.4.181" % "test"
)
