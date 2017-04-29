name := "algorithmia-scala"
organization := "com.algorithmia"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.11"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.json4s" %% "json4s-native" % "3.5.1",
  // Test
  "commons-io" % "commons-io" % "2.5" % Test,
  "commons-codec" % "commons-codec" % "1.10" % Test,
  "junit" % "junit" % "4.12" % Test,
  "org.specs2" %% "specs2" % "3.7" % Test
)
