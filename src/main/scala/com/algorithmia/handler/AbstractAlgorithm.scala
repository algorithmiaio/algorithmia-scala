package com.algorithmia.handler
import play.api.libs.json._

import scala.util.Try

trait AbstractAlgorithm[I, O] {
  def apply(input:I): Try[O]
  def load(): Try[Unit]
  implicit def inputReader: Reads[I] = Json.reads[I]
  implicit def outputWriter: Writes[O] = Json.writes[O]
}
