package com.algorithmia.handler
import scala.reflect.runtime.universe.Type
import play.api.libs.json._

import scala.util._

case class RequestHandler[I](inputType: Type) {

  def convertToInput(input: JsValue)(implicit s: Reads[I]): Try[I] = {
    Json.fromJson[I](input).asOpt match {
      case Some(value) => Success(value)
      case None => Failure(new Exception(s"Unable to convert ${input} into ${inputType}"))
    }
  }

  def processRequest(line: String)(implicit s: Reads[I]): Try[I] = {
    val jsvalue = Json.parse(line)
    (jsvalue \ "data") match {
      case JsDefined(value) => convertToInput(value)
      case JsUndefined() => Failure(new Exception(s"unable to find key 'data' in request json object:\n${line}"))
    }
  }

}