name := "GlintForFlink"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "ch.ethz.inf.da" %% "glint" % "0.1-SNAPSHOT" exclude("org.slf4j", "slf4j-log4j12")

libraryDependencies += "org.apache.flink" %% "flink-scala" % "1.2.0" exclude("org.slf4j", "slf4j-log4j12")

libraryDependencies += "org.apache.flink" %% "flink-streaming-scala" % "1.2.0" exclude("org.slf4j", "slf4j-log4j12")
