name := "algorithmia"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies += "org.scalaj" % "scalaj-http_2.11" % "1.1.4"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.10"
