scalaVersion := "2.11.8"

organization := "com.example"

version := "0.1-SNAPSHOT"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.4.12"

libraryDependencies += "ch.ethz.inf.da" %% "glint" % "0.1-SNAPSHOT" exclude("org.slf4j", "slf4j-log4j12")

libraryDependencies += "org.apache.flink" %% "flink-scala" % "1.2.0" exclude("org.slf4j", "slf4j-log4j12")

libraryDependencies += "org.apache.flink" %% "flink-streaming-scala" % "1.2.0" exclude("org.slf4j", "slf4j-log4j12")

