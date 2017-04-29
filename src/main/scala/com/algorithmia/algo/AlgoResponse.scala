package com.algorithmia.algo

import org.json4s._
import org.json4s.native.JsonMethods._

/**
 * Represents an algorithm response.
 */
sealed trait AlgoResponse {
  def as[T](implicit reader: Reader[T]): T
  def asOpt[T](implicit reader: Reader[T]): Option[T]
  def map[T](f: AlgoSuccess => T): Option[T]
  def metadata: Metadata
  /** Returns the full response from the server */
  def rawOutput: String
}

case class AlgoSuccess(result: JValue, metadata: Metadata, rawOutput: String) extends AlgoResponse {
  override def as[T](implicit reader: Reader[T]): T = reader.read(result)
  override def asOpt[T](implicit reader: Reader[T]): Option[T] = try {
    Some(reader.read(result))
  } catch {
    case e: Exception => None
  }
  override def map[T](f: AlgoSuccess => T): Option[T] = Some(f(this))
}

case class AlgoFailure(message: String, metadata: Metadata, rawOutput: String) extends AlgoResponse {
  override def as[T](implicit reader: Reader[T]): T = throw new ClassCastException("Algorithm failure response cannot be cast")
  override def asOpt[T](implicit reader: Reader[T]): Option[T] = None
  override def map[T](f: AlgoSuccess => T): Option[T] = None
}

object AlgoResponse {
  private implicit val formats = DefaultFormats

  /**
   * Parse server response into an AlgoResponse
   */
  def apply(rawOutput: String, outputType: AlgorithmOutputType): AlgoResponse = {
    val json = parse(rawOutput)
    json.extract[AlgoResponseRaw] match {
      case AlgoResponseRaw(_, Some(error), metadata) => AlgoFailure(error.message, metadata.metadata, rawOutput)
      case AlgoResponseRaw(result, None, metadata) => AlgoSuccess(result, metadata.metadata, rawOutput)
    }
  }

  /**
   * Case classes used to help with JSON parsing
   */
  private case class AlgoResponseRaw(
    result: JValue,
    error: Option[AlgoResponseError],
    metadata: AlgoResponseMetadata
  )
  private case class AlgoResponseError(message: String)
  private case class AlgoResponseMetadata(
    duration: Double,
    content_type: String,
    stdout: Option[String]
  ) {
    def metadata: Metadata = Metadata(
      duration, ContentType(content_type), stdout
    )
  }

}