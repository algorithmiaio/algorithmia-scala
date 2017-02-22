package com.algorithmia.data

import com.algorithmia._
import java.net.URLEncoder

sealed trait DataObjectType
case object DataFileType extends DataObjectType
case object DataDirectoryType extends DataObjectType

abstract class DataObject(client: AlgorithmiaClient, dataUrl: String, objectType: DataObjectType) {

  val path = dataUrl.replaceAll("^data://|^/", "")
  val trimmedPath = if(path.endsWith("/")) path.dropRight(1) else path

  val url = Algorithmia.apiBaseUrl + "/v1/data/" + URLEncoder.encode(path, "UTF-8")

  // Abstract methods
  def exists: Boolean

  def getName: String = {
  	trimmedPath.substring(trimmedPath.lastIndexOf("/") + 1)
  }

  def getParent: DataDirectory = {
    new DataDirectory(client, trimmedPath.replaceFirst("/[^/]+$", ""))
  }

  override def toString(): String = path

}
