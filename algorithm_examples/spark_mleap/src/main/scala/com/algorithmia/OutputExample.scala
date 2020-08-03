package com.algorithmia

case class OutputExample(output: String)

object OutputExample{
  implicit val writes: Reads[OutputExample] = Json.writes[OutputExample]
}