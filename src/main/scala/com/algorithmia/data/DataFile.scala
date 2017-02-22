package com.algorithmia.data

import com.algorithmia._
import java.io.File

class DataFile(client: AlgorithmiaClient, dataUrl: String) extends DataObject(client, dataUrl, DataFileType) {

  override def exists: Boolean = {
    ??? // TODO
  }

  def getFile(): File = {
    ??? // TODO
  }

  def put(file: File): Unit = {
    ??? // TODO
  }

  def delete(): Unit = {
    ??? // TODO
  }

}
