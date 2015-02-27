scalaVersion in ThisBuild := "2.11.5"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

organization := "io.strongtyped"

crossScalaVersions := Seq("2.10.4", "2.11.5")

fork in run := true