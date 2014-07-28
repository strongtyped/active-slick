
name := "active-slick"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "2.0.2",
  "org.slf4j" % "slf4j-nop" % "1.6.4" % "test",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "com.h2database" % "h2" % "1.3.166" % "test"
)
