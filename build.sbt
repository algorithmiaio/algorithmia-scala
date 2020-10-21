name := "algorithmia-scala"
organization := "com.algorithmia"

version := "1.0.2"

scalaVersion := "2.13.0"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.7.4",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "commons-codec" % "commons-codec" % "1.12",
  "org.apache.commons" % "commons-lang3" % "3.9",
  "com.google.code.gson" % "gson" % "2.8.6",
  // Test
  "commons-io" % "commons-io" % "2.6" % Test,
  "junit" % "junit" % "4.12" % Test,
  // "org.specs2" %% "specs2" % "3.7" % Test
  "com.typesafe.play" %% "play-specs2" % "2.7.3" % Test,
  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
)

// Build for multiple scala versions
crossScalaVersions := Seq("2.11.12", "2.12.8", "2.13.0")
