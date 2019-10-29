package com.algorithmia.handler
import play.api.libs.json._

trait AbstractAlgorithm[I, O] {
  def apply(input:I): O
  def load(): Either[Throwable, Unit]
  def inputReader: Reads[I]
  def outputWriter: Writes[O]
}
