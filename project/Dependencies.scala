
import sbt._

object Dependencies {

  val mainDependencies = Seq(
    "com.typesafe.slick"    %%  "slick"                   % "2.0.0",
    "org.slf4j"             %   "slf4j-nop"               % "1.6.4"
  )

  val mainTestDependencies = Seq (
    "org.scalatest"           %% "scalatest"              % "2.2.0"    % "test",
    "com.h2database"        %   "h2"                      % "1.3.166"  % "test"
  )
}