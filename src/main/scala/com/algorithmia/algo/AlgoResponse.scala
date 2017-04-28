package com.algorithmia.algo

import org.json4s.{DefaultFormats, JValue, Reader}

sealed trait AlgoResponse {
  def as[T](implicit reader: Reader[T]): T
  def asOpt[T](implicit reader: Reader[T]): Option[T]
  def map[T](f: AlgoSuccess => T): Option[T]
  def metadata: Metadata
}

case class AlgoSuccess(result: JValue, metadata: Metadata) extends AlgoResponse {
  override def as[T](implicit reader: Reader[T]): T = reader.read(result)
  override def asOpt[T](implicit reader: Reader[T]): Option[T] = try {
    Some(reader.read(result))
  } catch {
    case e: Exception => None
  }
  override def map[T](f: AlgoSuccess => T): Option[T] = Some(f(this))
}

case class AlgoFailure(message: String, metadata: Metadata) extends AlgoResponse {
  override def as[T](implicit reader: Reader[T]): T = throw new ClassCastException("Algorithm failure response cannot be cast")
  override def asOpt[T](implicit reader: Reader[T]): Option[T] = None
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