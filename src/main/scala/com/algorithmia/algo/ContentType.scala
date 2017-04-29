package com.algorithmia.algo

sealed trait ContentType {
  def content_type: String
}

case object ContentTypeVoid extends ContentType {
  def content_type: String = "void"
}

case object ContentTypeText extends ContentType {
  def content_type: String = "text"
}

case object ContentTypeJson extends ContentType {
  def content_type: String = "json"
}

case object ContentTypeBinary extends ContentType {
  def content_type: String = "binary"
}

object ContentType {
  def apply(content_type: String): ContentType = content_type match {
    case "void" => ContentTypeVoid
    case "text" => ContentTypeText
    case "json" => ContentTypeJson
    case "binary" => ContentTypeBinary
  }
}
