package com.algorithmia.algo

import org.json4s.{DefaultFormats, JValue}

sealed trait AlgoResponse {
  def map[T](f: AlgoSuccess => T): Option[T]
  def metadata: Metadata
}

case class AlgoSuccess(result: JValue, metadata: Metadata) extends AlgoResponse {
  override def map[T](f: AlgoSuccess => T): Option[T] = Some(f(this))
}

case class AlgoFailure(message: String, metadata: Metadata) extends AlgoResponse {
  override def map[T](f: AlgoSuccess => T): Option[T] = None
}

object AlgoResponse {
  private implicit val formats = DefaultFormats

  def fromJson(json: JValue): AlgoResponse = {
    json.extract[AlgoResponseRaw] match {
      case AlgoResponseRaw(_, Some(error), metadata) => AlgoFailure(error.message, metadata)
      case AlgoResponseRaw(result, None, metadata) => AlgoSuccess(result, metadata)
    }
  }

  private case class AlgoResponseRaw(
    result: JValue,
    error: Option[AlgoResponseError],
    metadata: Metadata
  )
  private case class AlgoResponseError(message: String)

}