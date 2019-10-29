package com.algorithmia.handler
import scala.reflect.runtime.universe.Type
import play.api.libs.json._

case class RequestHandler[I](inputType: Type) {

  def convertToInput(input: JsValue)(implicit s: Reads[I]): Either[Exception, I] = {
    Json.fromJson[I](input).asEither match {
      case Right(value) => Right(value)
      case Left(_) => Left(new Exception(s"Unable to convert ${input} into ${inputType}"))
    }
  }

  def processRequest(line: String)(implicit s: Reads[I]): Either[Exception, I] = {
    val jsvalue = Json.parse(line)
    val data = (jsvalue \ "data").get
    val result: I  = convertToInput(data).getOrElse(return Left(_))
  }

}