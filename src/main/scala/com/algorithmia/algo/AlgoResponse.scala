package com.algorithmia.algo

import java.io.IOException
import org.apache.commons.codec.binary.Base64
import play.api.libs.json._

/**
  * Represents an algorithm response.
  */
sealed trait AlgoResponse {
  def isSuccess: Boolean
  def as[T](implicit reads: Reads[T]): T
  def asOpt[T](implicit reads: Reads[T]): Option[T]
  def asString: String
  def asBytes: Array[Byte]
  def map[T](f: AlgoSuccess => T): Option[T]
  def metadata: Metadata

  /** Returns the full response from the server */
  def rawOutput: String
}

case class AlgoSuccess(result: JsValue, metadata: Metadata, rawOutput: String) extends AlgoResponse {
  override def isSuccess: Boolean = true
  override def as[T](implicit reads: Reads[T]): T = {
    metadata.content_type match {
      case ContentTypeJson | ContentTypeText => {
        Json.fromJson[T](result) match {
          case JsSuccess(obj, _) => obj
          case JsError(errors) => {
            val errorMsg = errors.map(_._2.mkString(",")).mkString("\n")
            throw new IOException("Failed to parse algorithm response: " + errorMsg)
          }
        }
      }
      case _ => throw new IOException("Cannot cast algorithm response")
    }
  }
  override def asOpt[T](implicit reads: Reads[T]): Option[T] = Json.fromJson[T](result).asOpt
  override def asString: String = result match {
    case JsString(str) => str
    case _ => result.toString
  }
  override def asBytes: Array[Byte] = metadata.content_type match {
    case ContentTypeBinary =>
      result match {
        case JsString(str) => Base64.decodeBase64(str) // Decode base 64
        case _ => throw new IOException("Algorithm returned successfully, but cannot be converted to byte array")
      }
    case _ => throw new IOException("Algorithm returned successfully, but cannot be converted to byte array")
  }
  override def map[T](f: AlgoSuccess => T): Option[T] = Some(f(this))
}

case class AlgoFailure(message: String, metadata: Metadata, rawOutput: String) extends AlgoResponse {
  override def isSuccess: Boolean = false
  override def as[T](implicit reads: Reads[T]): T =
    throw new ClassCastException("Algorithm returned an error, and cannot be converted to type")
  override def asOpt[T](implicit reads: Reads[T]): Option[T] = None
  override def asString: String =
    throw new ClassCastException("Algorithm returned an error, and cannot be converted to  string")
  override def asBytes: Array[Byte] =
    throw new ClassCastException("Algorithm returned an error, and cannot be converted to byte array")
  override def map[T](f: AlgoSuccess => T): Option[T] = None
}

object AlgoResponse {

  private implicit val algoResponseErrorReads = Json.reads[AlgoResponseError]
  private implicit val algoResponseMetadataReads = Json.reads[AlgoResponseMetadata]
  private implicit val algoResponseRawReads = Json.reads[AlgoResponseRaw]

  /**
    * Parse server response into an AlgoResponse
    */
  def apply(rawOutput: String, outputType: AlgorithmOutputType): AlgoResponse = {
    val json = Json.parse(rawOutput)
    Json.fromJson[AlgoResponseRaw](json) match {
      case JsSuccess(AlgoResponseRaw(result, None, metadata), _) => AlgoSuccess(result, metadata.metadata, rawOutput)
      case JsSuccess(AlgoResponseRaw(_, Some(error), metadata), _) =>
        AlgoFailure(error.message, metadata.metadata, rawOutput)
      case JsError(_) =>
        AlgoFailure("Failed to parse algorithm response", Metadata(0, ContentTypeVoid, None), rawOutput)
    }
  }

  /**
    * Case classes used to help with JSON parsing
    */
  private case class AlgoResponseRaw(
      result: JsValue,
      error: Option[AlgoResponseError],
      metadata: AlgoResponseMetadata
  )
  private case class AlgoResponseError(message: String)
  private case class AlgoResponseMetadata(
      duration: Double,
      content_type: Option[String],
      stdout: Option[String]
  ) {
    def metadata: Metadata = Metadata(
      duration,
      ContentType(content_type.getOrElse("")),
      stdout
    )
  }

}
