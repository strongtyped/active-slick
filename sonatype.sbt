sonatypeProfileName := "io.strongtyped"

credentials in Global ++= (for {
  username <- Option(System.getenv().get("SONATYPE_USERNAME"))
  password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq


pomExtra in Global := {
  <url>https://github.com/strongtyped/active-slick</url>
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
}

publishTo in Global  := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}