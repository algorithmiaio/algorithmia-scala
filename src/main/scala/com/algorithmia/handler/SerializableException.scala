package com.algorithmia.handler

import org.apache.commons.lang3.exception.ExceptionUtils
import play.api.libs.json._

case class Content(message: String, stacktrace: String, error_type: String)
case class SerializableException(error: Content)

object SerializableException {
  def fromException(e: Throwable): SerializableException = {
    val stackTrace = ExceptionUtils.getStackTrace(e)
    val message = ExceptionUtils.getMessage(e)
    val errorType = e.getClass.toString
    val content = Content(message, stackTrace, errorType)
    SerializableException(content)
  }

  implicit def writes: Writes[SerializableException] = Json.writes[SerializableException]
}

object Content {
  implicit def writes: Writes[Content] = Json.writes[Content]
}
