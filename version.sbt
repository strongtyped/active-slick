
val snapshotSuffix = "-SNAPSHOT"

version in ThisBuild := "0.2.2" + snapshotSuffix

isSnapshot := version.value.endsWith(snapshotSuffix)