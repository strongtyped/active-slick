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
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick-codegen" % slickVersion,
      "com.h2database" % "h2" % "1.4.187",
      "org.slf4j" % "slf4j-nop" % "1.7.12", // To silence build warnings
      scalaTest
    ),
    slick <<= slickCodeGenTask,
    sourceGenerators in Compile <+= slickCodeGenTask
  ) ++ mainDeps
).dependsOn(activeSlick % "compile->compile;test->test")
//======+=========================================

lazy val slick = TaskKey[Seq[File]]("gen-tables")

lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (srcManaged, cp, r, s) =>
  val pkg = "io.strongtyped.active.slick.docexamples.codegen"
  val url = "jdbc:h2:mem:test;INIT=runscript from 'modules/samples/src/main/resources/codegen_schema.sql'" // connection info for a pre-populated throw-away, in-memory db for this demo, which is freshly initialized on every run
val jdbcDriver = "org.h2.Driver"
  val slickDriver = "slick.driver.H2Driver"
  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, srcManaged.getPath, pkg), s.log))
  val outputDir = srcManaged / pkg.replace(".", "/")
  Seq(outputDir / "Tables.scala")
}