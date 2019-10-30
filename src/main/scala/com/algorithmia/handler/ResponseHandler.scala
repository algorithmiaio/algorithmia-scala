package com.algorithmia.handler

import java.io.FileOutputStream
import java.io.PrintStream

import play.api.libs.json.{Json, Writes}

import scala.util.Try

case class ResponseHandler[O](){
  val FIFOPATH = "/tmp/algoout"


  private def write(data: String): Try[Unit] = {
    Try(new PrintStream(new FileOutputStream(this.FIFOPATH, true)))
        .map({s => s.println(data); s})
        .map({s => s.flush(); s})
      .map(_.close())
    }

  def writeErrorToPipe(e: Throwable): Try[Unit] = {
    val serializable = SerializableException.fromException(e)
    val serialized = Json.toJson(serializable)
    write(serialized.toString())
  }

  def writeResponseToPipe(output: O)(implicit  writer: Writes[O]): Try[Unit] = {
    val response = Response(Metadata("text"), output)
    val serialized = Json.toJson(response)
    write(serialized.toString())
  }
}
