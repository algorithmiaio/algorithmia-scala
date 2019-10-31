package com.algorithmia.data

import com.algorithmia._
import java.net.URLEncoder

sealed trait DataObjectType
case object DataFileType extends DataObjectType
case object DataDirectoryType extends DataObjectType

abstract class DataObject(val client: AlgorithmiaClient, dataUrl: String, objectType: DataObjectType) {

  val path: String = dataUrl.replaceAll("^data://|^/", "")
  val trimmedPath: String = if (path.endsWith("/")) path.dropRight(1) else path

  val url: String = AlgorithmiaConf.apiAddress + "/v1/data/" + URLEncoder.encode(path, "UTF-8")

  // Abstract methods
  def exists: Boolean

  def getType: DataObjectType
  def isDirectory: Boolean = getType == DataDirectoryType
  def isFile: Boolean = getType == DataFileType

  def getName: String = {
    trimmedPath.substring(trimmedPath.lastIndexOf("/") + 1)
  }

  def getParent: DataDirectory = {
    new DataDirectory(client, trimmedPath.replaceFirst("/[^/]+$", ""))
  }

  override def toString: String = path

}
