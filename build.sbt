name := "algorithmia-scala"
organization := "com.algorithmia"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.json4s" %% "json4s-native" % "3.5.0",
  "org.json4s" %% "muster-codec-json4s" % "0.3.0",
  "junit" % "junit" % "4.12" % "test"
)
