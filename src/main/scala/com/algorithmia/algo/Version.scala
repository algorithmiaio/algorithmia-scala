package com.algorithmia.algo

import scala.util.Try

abstract sealed trait Version {
  def toString: String
}

object Version{
  case object Latest extends Version {
    override def toString: String = "latest"
  }
  case class Minor(major: Int, minor: Int) extends Version {
    override def toString: String = s"${major}.${minor}"
  }
  case class Revision(major: Int, minor: Int, revision: Int) extends Version {
    override def toString: String = s"${major}.${minor}.${revision}"
  }

  @throws(classOf[VersionParseException])
  def fromString(version: String): Version = {
    version.split('.') match {
      case Array(x, y, z) => Revision(x.toInt,y.toInt,z.toInt)
      case Array(x, y) => Minor(x.toInt,y.toInt)
      case Array("latest") | Array("") => Latest
      case other => {
        println(s"DEBUG: $other")
        throw new VersionParseException(version)
      }
    }
  }
}

case class VersionParseException(version: String) extends Exception(version)
