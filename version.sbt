val snapshotSuffix = "-SNAPSHOT"

version in ThisBuild := "0.3.4" //+ snapshotSuffix

isSnapshot := version.value.endsWith(snapshotSuffix)
