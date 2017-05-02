package com.algorithmia.data

import com.algorithmia._
import java.io.{File, IOException}
import play.api.libs.json._

class DataDirectory(client: AlgorithmiaClient, dataUrl: String) extends DataObject(client, dataUrl, DataDirectoryType) {

  private val defaultAcl = DataAcl(read = DataPrivate)

  override def exists: Boolean = {
    client.http.head(url).code == 200
  }

  def getType: DataObjectType = DataDirectoryType

  private case class CreateDirectoryRequest(name: String, acl: DataAcl)
  private implicit val createDirectoryRequestWrites: Writes[CreateDirectoryRequest] = Json.writes[CreateDirectoryRequest]

  def create(dataAcl: DataAcl = defaultAcl): Unit = {
    val req = CreateDirectoryRequest(getName, dataAcl)
    val reqJson = Json.toJson(req).toString
    client.http.post(getParent.url, reqJson)
  }

  def file(filename: String): DataFile = {
    new DataFile(client, trimmedPath + "/" + filename)
  }

  def files: Iterable[DataFile] = {
    ???
  }

  def dirs: Iterable[DataFile] = {
    ???
  }

  def putFile(file: File): DataFile = {
    val dataFile = new DataFile(client, trimmedPath + "/" + file.getName)
    dataFile.put(file)
    dataFile
  }

  def delete(forceDelete: Boolean): Unit = {
    client.http.delete(s"$url?force=$forceDelete")
  }

  private case class DirectoryListResponse(acl: DataAcl)
  private implicit val directoryListResponseReads: Reads[DirectoryListResponse] = Json.reads[DirectoryListResponse]

  def getPermissions: DataAcl = {
    // To get permissions, list the directory and extract the acl field
    val response = client.http.get(url + "?acl=true")
    if(response.code == 200) {
      val responseJson = Json.parse(response.body)
      val listing = Json.fromJson[DirectoryListResponse](responseJson)
      Json.fromJson[DirectoryListResponse](responseJson) match {
        case JsSuccess(listing, _) => listing.acl
        case JsError(_) => throw new IOException("Failed to parse permissions")
      }
    } else {
      throw new IOException("Failed to get permissions, status code " + response.code)
    }
  }

  private case class UpdateDirectoryRequest(acl: DataAcl)
  private implicit val updateDirectoryResponseWrites: Writes[UpdateDirectoryRequest] = Json.writes[UpdateDirectoryRequest]

  def updatePermissions(dataAcl: DataAcl): Boolean = {
    val req = UpdateDirectoryRequest(dataAcl)
    val reqJson = Json.toJson(req).toString
    val response = client.http.patch(url, reqJson)
    response.code == 200
  }

}
