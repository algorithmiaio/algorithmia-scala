package com.algorithmia.handler
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

trait AbstractAlgorithm[I <: AnyVal, O <: AnyVal] {
  def apply(input:I): Try[O]
  def load(): Try[Unit] = Success(())
  implicit def inputReader: Reads[I] = Try(implicitly[Reads[I]]) match {
    case Failure(_) => Json.reads[I]
    case Success(s: Reads[I]) => s
  }
  implicit def outputWriter: Writes[O] = Try(implicitly[Writes[O]]) match {
    case Failure(_) => Json.writes[O]
    case Success(s: Writes[O]) => s
  }
}
