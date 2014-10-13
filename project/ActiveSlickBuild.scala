import sbt._
import sbt.Keys._


object ActiveSlickBuild extends Build with BuildSettings with Dependencies {

  
  lazy val root = Project(
    id = "active-slick-root",
    base = file("."),
    settings = projectSettings ++ Seq(
      publishArtifact := false,
      libraryDependencies ++= mainDependencies ++ mainTestDependencies
    )
  ) aggregate(activeSlick, samples)

  
  
  // CORE ==========================================
  lazy val activeSlick: Project = Project(
    id = "active-slick",
    base = file("modules/core"),
    settings = projectSettings ++ Seq(
      libraryDependencies ++= mainDependencies ++ mainTestDependencies
      )
  ) 
  //================================================



  // SAMPLES =======================================
  // contains examples used on the docs, not intented to be released
  lazy val samples: Project = Project(
    id = "active-slick-samples",
    base = file("modules/samples"),
    settings = projectSettings ++ Seq(
      publishArtifact := false,
      libraryDependencies ++= mainDependencies ++ macroDeps
    )
  ) dependsOn activeSlick
  //================================================
  
}