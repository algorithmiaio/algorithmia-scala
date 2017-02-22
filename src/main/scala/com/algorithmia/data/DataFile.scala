package com.algorithmia.data

import com.algorithmia._
import java.io.File
import java.nio.file.Files

class DataFile(client: AlgorithmiaClient, dataUrl: String) extends DataObject(client, dataUrl, DataFileType) {

  override def exists: Boolean = {
    client.http.head(url).code == 200
  }

  def getFile(): File = {
    val is = client.http.getInputStream(url).body
    val file = new File(getName)
    Files.copy(is, file.toPath)
    file
  }

  def getString(): String = {
    client.http.get(url).body
  }

  def getBytes(): Array[Byte] = {
    client.http.getBytes(url).body
  }

  def put(data: String): Unit = {
    client.http.put(url, data.getBytes)
  }

  def put(file: File): Unit = {
    val bytes = Files.readAllBytes(file.toPath)
    client.http.put(url, bytes)
  }

  def delete(): Unit = {
    client.http.delete(url)
  }

}
