val snapshotSuffix = "-SNAPSHOT"

version in ThisBuild := "0.3.5-M1" // + snapshotSuffix

isSnapshot := version.value.endsWith(snapshotSuffix)
