import sbt._

trait Dependencies { this:Build with BuildSettings => 

  val slick         =   "com.typesafe.slick"      %%  "slick"         % "2.1.0"
  val scalaTest     =   "org.scalatest"           %%  "scalatest"     % "2.2.1"    % "test"
  val h2database    =   "com.h2database"          %   "h2"            % "1.4.181"  % "test"


  val mainDependencies = Seq(
    slick
  )

  val mainTestDependencies = Seq (
    scalaTest, h2database
  )

  
}