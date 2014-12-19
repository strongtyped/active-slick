import sbt._
import sbt.Keys.scalaVersion

trait Dependencies { this:Build =>


  val slick         =   "com.typesafe.slick"      %%  "slick"         % "2.1.0"
  val shapeless = Def setting (
      CrossVersion partialVersion scalaVersion.value match {

        case Some((2, scalaMajor)) if scalaMajor >= 11 =>
          "com.chuusai" %% "shapeless" % "2.0.0"

        case Some((2, 10)) =>
          "com.chuusai" %  "shapeless" % "2.0.0" cross CrossVersion.full
      }
    )

  val scalaTest     =   "org.scalatest"           %%  "scalatest"     % "2.2.1"    % "test"
  val h2database    =   "com.h2database"          %   "h2"            % "1.4.181"  % "test"

}