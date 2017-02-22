package com.algorithmia.data

import com.algorithmia._
import java.io.File

class DataFile(client: AlgorithmiaClient, dataUrl: String) extends DataObject(client, dataUrl, DataFileType) {

  override def exists: Boolean = {
    client.head(url).code == 200
  }

  def getFile(): File = {
    ??? // TODO
  }

  def put(data: String): Unit = {
    client.put(url, data)
  }

  def put(file: File): Unit = {
    ??? // TODO
  }

  def delete(): Unit = {
    client.delete(url)
  }

}
