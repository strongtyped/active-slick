import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform

object ActiveSlickBuild extends Build with Dependencies {

  val buildName         = "ActiveSlick"
  val appVersion        = "0.0.1-SNAPSHOT"
  val scalaBuildOptions = Seq("-unchecked", "-deprecation", "-feature", "-Xlint")


  val buildSettings = Seq(
    scalaVersion := "2.11.2",
    scalacOptions := scalaBuildOptions,
    version := appVersion
  ) ++ SbtScalariform.scalariformSettings

  
  lazy val root = Project(
    id = "active-slick-root",
    base = file("."),
    settings = buildSettings ++ Seq(
      publishArtifact := false,
      libraryDependencies ++= mainDependencies ++ mainTestDependencies
    )
  ) aggregate(activeSlick, samples)

  
  
  // CORE ==========================================
  lazy val activeSlick: Project = Project(
    id = "active-slick",
    base = file("modules/core"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= mainDependencies ++ mainTestDependencies
      )
  ) 
  //================================================



  // SAMPLES =======================================
  lazy val samples: Project = Project(
    id = "active-slick-samples",
    base = file("modules/samples"),
    settings = buildSettings ++ Seq(
      publishArtifact := false,
      libraryDependencies ++= mainDependencies ++ macroDeps
    )
  ) dependsOn activeSlick
  //================================================
  
}