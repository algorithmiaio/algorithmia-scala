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
