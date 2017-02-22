package com.algorithmia.data

import com.algorithmia._
import java.io.File
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

class DataDirectory(client: AlgorithmiaClient, dataUrl: String) extends DataObject(client, dataUrl, DataDirectoryType) {

  override def exists: Boolean = {
    client.http.head(url).code == 200
  }

  case class CreateDirectoryRequest(name: String, acl: Option[DataAcl])
  def create(dataAcl: Option[DataAcl] = None): Unit = {
    val req = CreateDirectoryRequest(getName, None)
    val reqJson =
    ??? // TODO
  }

  def file(filename: String): DataFile = {
    new DataFile(client, trimmedPath + "/" + filename)
  }

  def putFile(file: File): DataFile = {
    val dataFile = new DataFile(client, trimmedPath + "/" + file.getName)
    dataFile.put(file)
    dataFile
  }

  def delete(forceDelete: Boolean): Unit = {
    client.http.delete(s"$url?force=$forceDelete")
  }

  def getPermissions(): DataAcl = {
    ??? // TODO
  }

  case class UpdateDirectoryRequest(acl: DataAcl)
  def updatePermissions(dataAcl: DataAcl): Boolean = {
    val req = UpdateDirectoryRequest(dataAcl)
    // val reqJson = compact(render(req))
    // client.http.patch(url, reqJson)
    ??? // TODO
  }

}
