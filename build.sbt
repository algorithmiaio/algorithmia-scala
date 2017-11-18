name := "algorithmia-scala"
organization := "com.algorithmia"

version := "0.9.5-SNAPSHOT"

scalaVersion := "2.11.12"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "commons-codec" % "commons-codec" % "1.11",
  // Test
  "commons-io" % "commons-io" % "2.6" % Test,
  "junit" % "junit" % "4.12" % Test,
  // "org.specs2" %% "specs2" % "3.7" % Test
  "com.typesafe.play" %% "play-specs2" % "2.6.7" % Test
)

// Build for multiple scala versions
crossScalaVersions := Seq("2.11.12", "2.12.4")
