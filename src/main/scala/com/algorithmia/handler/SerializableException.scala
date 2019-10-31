package com.algorithmia.handler

import org.apache.commons.lang3.exception.ExceptionUtils
import play.api.libs.json._

case class SerializableException(message: String, stacktrace: String, error_type: String) {
}

object SerializableException {
  def fromException(e: Throwable): SerializableException = {
    val stackTrace = ExceptionUtils.getStackTrace(e)
    val message = ExceptionUtils.getMessage(e)
    val errorType = e.getClass.toString
    SerializableException(message, stackTrace, errorType)
  }

  implicit def writes: Writes[SerializableException] = Json.writes[SerializableException]
}
