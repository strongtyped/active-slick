import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform

trait BuildSettings { this:Build => 

	val ScalaVersion = "2.11.2"
	
	val Organization = "io.strongtyped"

	val ScalacOptions = Seq("-unchecked", "-deprecation", "-feature", "-Xlint")

	val projectSettings = Seq(
		organization := Organization,
		scalaVersion := ScalaVersion,
		scalacOptions := ScalacOptions
	) ++ SbtScalariform.scalariformSettings

  
}