import Dependencies._
import BuildSettings._

scalaVersion in ThisBuild := "2.11.6"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

organization in ThisBuild := "io.strongtyped"

crossScalaVersions := Seq("2.10.5", "2.11.6")

parallelExecution in Test := false


lazy val root = Project(
  id = "active-slick-root",
  base = file("."),
  settings = projSettings ++ Seq(
    publishArtifact := false
  )
) aggregate(activeSlick, shapelessIntegration, samples)


// Core ==========================================
lazy val activeSlick = Project(
  id = "active-slick",
  base = file("modules/core"),
  settings = projSettings ++ mainDeps ++ testDeps
)
//================================================


// Shapeless Integration  ========================
lazy val shapelessIntegration = {

  // settings to include shapeless dependencies
  val shapeless = Seq(
    libraryDependencies ++= shapelessDeps.value
  )

  Project(
    id = "active-slick-shapeless",
    base = file("modules/shapeless"),
    settings = projSettings ++ mainDeps ++ shapeless ++ testDeps
  ) dependsOn activeSlick
}
//================================================


// Samples =======================================
// contains examples used on the docs, not intended to be released
lazy val samples = Project(
  id = "active-slick-samples",
  base = file("modules/samples"),
  settings = projSettings ++ Seq(
    publishArtifact := false
  ) ++ mainDeps
) dependsOn activeSlick
//======+=========================================
