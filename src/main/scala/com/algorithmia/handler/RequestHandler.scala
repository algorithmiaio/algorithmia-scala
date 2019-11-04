package com.algorithmia.handler

import play.api.libs.json._

import scala.reflect.ClassTag
import scala.util._

case class RequestHandler[I]() {

  def processRequest(line: String)(implicit s: Reads[I], obj: ClassTag[I]): Try[I] = {
    (Json.parse(line) \ "data") match {
      case JsDefined(value) => Json.fromJson[I](value) match {
        case JsSuccess(value, _) => Success(value)
        case JsError(_) =>Failure(new Exception(s"Unable to convert ${line} into ${obj}"))
      }
      case JsUndefined() => Failure(new Exception(s"unable to find key 'data' in request json object:\n${line}"))
    }
  }

}
