
val snapshotSuffix = "-SNAPSHOT"

version in ThisBuild := "0.2.2"

isSnapshot := version.value.endsWith(snapshotSuffix)