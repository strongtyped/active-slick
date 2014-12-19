import sbt._
import sbt.Keys._


object ActiveSlickBuild extends Build with BuildSettings with Dependencies {

  
  lazy val root = Project(
    id = "active-slick-root",
    base = file("."),
    settings = projectSettings ++ Seq(
      publishArtifact := false
    )
  ) aggregate(activeSlick, samples)



  // CORE ==========================================
  lazy val activeSlick: Project = Project(
    id = "active-slick",
    base = file("modules/core"),
    settings = projectSettings ++ Seq(
      libraryDependencies ++= Seq(slick, shapeless.value) ++ Seq(h2database, scalaTest)
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
      libraryDependencies ++= Seq(slick, shapeless.value)
    )
  ) dependsOn activeSlick
  //================================================
  
}