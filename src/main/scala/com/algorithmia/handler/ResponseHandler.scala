package com.algorithmia.handler

import play.api.libs.json.Json

class ResponseHandler(){
  val pipePath = "/tmp/algoout"



  def writeErrorToPipe(e: Exception): Unit = {
    val serializable = SerializableException.fromException(e)
    write(serializable.serialize())
  }
}
