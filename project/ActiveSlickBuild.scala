import sbt._
import sbt.Keys._


object ActiveSlickBuild extends Build with BuildSettings with Dependencies {

  
  lazy val root = Project(
    id = "active-slick-root",
    base = file("."),
    settings = projectSettings ++ Seq(
      publishArtifact := false
    )
  ) aggregate(activeSlick, shapelessIntegration, samples)


  // Core ==========================================
  lazy val activeSlick = Project(
    id = "active-slick",
    base = file("modules/core"),
    settings = projectSettings ++ mainDeps ++ testDeps
  )
  //================================================



  // Shapeless Integration  ========================
  lazy val shapelessIntegration = {

    // settings to include shapeless dependencies
    val shapeless =  Seq(
      libraryDependencies ++= shapelessDeps.value
    )

    Project(
      id = "active-slick-shapeless",
      base = file("modules/shapeless"),
      settings = projectSettings ++ mainDeps ++ shapeless ++ testDeps
    ) dependsOn activeSlick
  } 
  //================================================



  // Samples =======================================
  // contains examples used on the docs, not intended to be released
  lazy val samples = Project(
    id = "active-slick-samples",
    base = file("modules/samples"),
    settings = projectSettings ++ Seq(
      publishArtifact := false
    ) ++ mainDeps
  ) dependsOn activeSlick
  //======+=========================================

  
}