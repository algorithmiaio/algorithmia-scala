name := "algorithmia-scala"
organization := "com.algorithmia"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies += "org.scalaj" %% "scalaj-http" % "1.1.6"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.0"
