name := "algorithmia-scala"
organization := "com.algorithmia"

version := "0.9.5-SNAPSHOT"

scalaVersion := "2.11.11"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.11",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "commons-codec" % "commons-codec" % "1.10",
  // Test
  "commons-io" % "commons-io" % "2.5" % Test,
  "junit" % "junit" % "4.12" % Test,
  "org.specs2" %% "specs2" % "3.7" % Test
)

// Build for multiple scala versions
crossScalaVersions := Seq("2.11.11", "2.12.3")
