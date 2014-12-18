import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform
import xerial.sbt.Sonatype.SonatypeKeys._
import xerial.sbt.Sonatype.sonatypeSettings

trait BuildSettings { this:Build => 

	val ScalacOptions = Seq("-unchecked", "-deprecation", "-feature", "-Xlint")

	val projectSettings = Seq(
    scalaVersion  := "2.11.4",
		scalacOptions := ScalacOptions
	) ++
    SbtScalariform.defaultScalariformSettings ++
    sonatypePublishSettings

  	def sonatypePublishSettings = sonatypeSettings ++ Seq(
  	  profileName		:= "io.strongtyped",
	    pomExtra 			:= <url>https://github.com/strongtyped/active-slick</url>
      <licenses>
        <license>
          <name>Apache-style</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>https://github.com/strongtyped/active-slick.git</url>
        <connection>scm:git:git@github.com:strongtyped/active-slick.git</connection>
      </scm>
      <developers>
        <developer>
          <id>@renatocaval</id>
          <name>Renato Cavalcanti</name>
          <url>http://www.strongtyped.io/</url>
        </developer>
      </developers>
	)
}