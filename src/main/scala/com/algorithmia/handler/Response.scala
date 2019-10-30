package com.algorithmia.handler

import play.api.libs.json.{JsValue, Json, Writes}

case class Metadata(content_type: String)

object Metadata {
  implicit val m: Writes[Metadata] = Json.writes[Metadata]
}

case class Response[O: Writes](metadata: Metadata, result: O)

object Response {
  implicit def ww[O](implicit writeO: Writes[O]) : Writes[Response[O]] = Json.writes[Response[O]]
}