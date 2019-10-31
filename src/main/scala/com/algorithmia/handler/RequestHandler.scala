package com.algorithmia.handler

import play.api.libs.json._

import scala.reflect.ClassTag
import scala.util._

case class RequestHandler[I]() {

  def convertToInput(input: JsValue)(implicit s: Reads[I], obj: ClassTag[I]): Try[I] = {
    Json.fromJson[I](input).asOpt match {
      case Some(value) => Success(value)
      case None => Failure(new Exception(s"Unable to convert ${input} into ${obj}"))
    }
  }

  def processRequest(line: String)(implicit s: Reads[I], obj: ClassTag[I]): Try[I] = {
    val jsvalue = Json.parse(line)
    (jsvalue \ "data") match {
      case JsDefined(value) => convertToInput(value)
      case JsUndefined() => Failure(new Exception(s"unable to find key 'data' in request json object:\n${line}"))
    }
  }

}
