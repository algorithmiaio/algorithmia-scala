package com.algorithmia.data

import com.algorithmia._
import java.io.{File, IOException}
import java.net.URLEncoder
import play.api.libs.json._

class DataDirectory(client: AlgorithmiaClient, dataUrl: String) extends DataObject(client, dataUrl, DataDirectoryType) {

  override def exists: Boolean = {
    client.http.head(url).code == 200
  }

  def getType: DataObjectType = DataDirectoryType

  private case class CreateDirectoryRequest(name: String, acl: Option[DataAcl])
  private implicit val createDirectoryRequestWrites: Writes[CreateDirectoryRequest] = Json.writes[CreateDirectoryRequest]

  def create(dataAcl: Option[DataAcl] = None): Unit = {
    val req = CreateDirectoryRequest(getName, dataAcl)
    val reqJson = Json.toJson(req).toString
    client.http.post(getParent.url, reqJson)
  }

  def file(filename: String): DataFile = {
    new DataFile(client, trimmedPath + "/" + filename)
  }

  def files: Iterable[DataFile] = {
    new Iterable[DataFile] {
      def iterator: Iterator[DataFile] = new DataFileIterator(DataDirectory.this)
    }
  }

  def dirs: Iterable[DataDirectory] = {
    new Iterable[DataDirectory] {
      def iterator: Iterator[DataDirectory] = new DataDirectoryIterator(DataDirectory.this)
    }
  }

  def putFile(file: File): DataFile = {
    val dataFile = new DataFile(client, trimmedPath + "/" + file.getName)
    dataFile.put(file)
    dataFile
  }

  def delete(forceDelete: Boolean): Unit = {
    client.http.delete(s"$url?force=$forceDelete")
  }

  private case class PermissionResponse(acl: DataAcl)
  private implicit val permissionResponseReads: Reads[PermissionResponse] = Json.reads[PermissionResponse]

  def getPermissions: DataAcl = {
    // To get permissions, list the directory and extract the acl field
    val response = client.http.get(url + "?acl=true")
    if(response.code == 200) {
      val responseJson = Json.parse(response.body)
      Json.fromJson[PermissionResponse](responseJson) match {
        case JsSuccess(listing, _) => listing.acl
        case error: JsError => throw new IOException(s"Failed to parse permissions $error $responseJson")
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

  case class FileMetadata(filename: String)
  case class DirectoryMetadata(name: String)
  case class DirectoryListResponse(
    files: Option[List[FileMetadata]],
    folders: Option[List[DirectoryMetadata]],
    marker: Option[String],
    acl: Option[DataAcl]
  )
  private implicit val fileMetadataReads: Reads[FileMetadata] = Json.reads[FileMetadata]
  private implicit val directoryMetadataReads: Reads[DirectoryMetadata] = Json.reads[DirectoryMetadata]
  private implicit val directoryListResponseReads: Reads[DirectoryListResponse] = Json.reads[DirectoryListResponse]

  /**
    * Gets a single page of the directory listing. Subsquent pages are fetched with the returned marker value.
    *
    * @param marker indicates the specific page to fetch; first page is fetched if None
    * @return a page of files and directories that exist within this directory
    */
  def getPage(marker: Option[String], getAcl: Boolean): DirectoryListResponse = {
    val markerParam = marker.map(m => "marker=" + URLEncoder.encode(m, "UTF-8"))
    val aclParam = if(getAcl) Some("marker=true") else None
    val params1 = (markerParam ++ aclParam).mkString("&")
    val params = if(params1.isEmpty) "" else "?" + params1
    val response = client.http.get(url + params)
    if(response.code == 200) {
      val responseJson = Json.parse(response.body)
      Json.fromJson[DirectoryListResponse](responseJson) match {
        case JsSuccess(listing,_) => listing
        case JsError(errors) => throw new IOException("Failed to parse listing page: " + errors)
      }
    } else {
      throw new IOException("Failed to fetch listing page, status code: " + response.code)
    }
  }

}
