package com.algorithmia.data

import com.algorithmia._
import java.io.{File, InputStream}
import java.nio.file.{Files, StandardCopyOption}
import scala.io.Source

class DataFile(client: AlgorithmiaClient, dataUrl: String) extends DataObject(client, dataUrl, DataFileType) {

  override def exists: Boolean = {
    client.http.head(url).code == 200
  }

  def getType: DataObjectType = DataFileType

  def getFile: File = {
    val response = client.http.getInputStream(url) { is =>
      // Create temp directory
      val dir = Files.createTempDirectory("algocache").toFile
      // Create file with same name as this object in temp dir
      val file = new File(dir, getName)
      // Copy inputstream to file
      Files.copy(is, file.toPath, StandardCopyOption.REPLACE_EXISTING)
      file
    }
    response.body
  }

  def getString: String = {
    client.http.get(url).body
  }

  def getBytes: Array[Byte] = {
    client.http.getBytes(url).body
  }

  def put(data: String): Unit = {
    client.http.put(url, data.getBytes)
  }

  def put(is: InputStream): Unit = {
    // TODO: Stream bytes
    val bytes = Source.fromInputStream(is).mkString("").getBytes("UTF-8")
    client.http.put(url, bytes)
  }

  def put(file: File): Unit = {
    val bytes = Files.readAllBytes(file.toPath)
    client.http.put(url, bytes)
  }

  def delete(): Unit = {
    client.http.delete(url)
  }

}
