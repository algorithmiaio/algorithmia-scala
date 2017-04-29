package com.algorithmia.data

import com.algorithmia._
import java.io.File

import org.json4s._
import org.json4s.native.Json
import org.json4s.native.JsonMethods._

class DataDirectory(client: AlgorithmiaClient, dataUrl: String) extends DataObject(client, dataUrl, DataDirectoryType) {
  private implicit val formats = DefaultFormats

  override def exists: Boolean = {
    client.http.head(url).code == 200
  }

  def getType: DataObjectType = DataDirectoryType

  case class CreateDirectoryRequest(name: String, acl: Option[DataAcl])
  def create(dataAcl: Option[DataAcl] = None): Unit = {
    val req = CreateDirectoryRequest(getName, dataAcl)
    val reqJson: JValue = new Json(DefaultFormats).decompose(req)
    client.http.post(url, reqJson.toString)
  }

  def file(filename: String): DataFile = {
    new DataFile(client, trimmedPath + "/" + filename)
  }

  def files: Iterable[DataFile] = {
    List.empty // TODO
  }

  def dirs: Iterable[DataFile] = {
    List.empty // TODO
  }

  def putFile(file: File): DataFile = {
    val dataFile = new DataFile(client, trimmedPath + "/" + file.getName)
    dataFile.put(file)
    dataFile
  }

  def delete(forceDelete: Boolean): Unit = {
    client.http.delete(s"$url?force=$forceDelete")
  }

  case class DirectoryListResponse(acl: DataAcl)
  def getPermissions: DataAcl = {
    // To get permissions, list the directory and extract the acl field
    val response = client.http.get(url + "?acl=true").body
    val listing = parse(response).extract[DirectoryListResponse]
    listing.acl
  }

  case class UpdateDirectoryRequest(acl: DataAcl)
  def updatePermissions(dataAcl: DataAcl): Boolean = {
    val req = UpdateDirectoryRequest(dataAcl)
    val reqJson: JValue = new Json(DefaultFormats).decompose(req)
    val response = client.http.patch(url, reqJson)
    response.code == 200
  }

}
