package com.algorithmia.algo

import org.json4s._
import org.json4s.native.JsonMethods._

case class PipeIO(value: String, totalDuration: Double = 0) {
  private implicit val formats = DefaultFormats

  @throws(classOf[AlgorithmError])
  @throws(classOf[AlgorithmApiError])
  def |(that: Algorithm): PipeIO = {
    that.pipe(this.value) match {
      case AlgoSuccess(result, metadata) => {
        result.extractOpt[AlgorithmOutput[JValue]] match {
          case Some(output) => PipeIO(compact(render(output.result)), this.totalDuration + output.metadata.duration)
          case None => throw new AlgorithmError(result.toString)
        }
      }
      case AlgoFailure(message, metadata) => throw new AlgorithmError(message)
    }
  }
}
