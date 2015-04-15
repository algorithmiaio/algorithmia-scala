package algorithmia.algorithms

import org.json4s._
import org.json4s.native.JsonMethods._

case class PipeIO(value: String, totalDuration: Double = 0) {
  implicit val formats = DefaultFormats

  @throws(classOf[AlgorithmError])
  @throws(classOf[AlgorithmApiError])
  def |(that: Algorithm): PipeIO = {
    val raw = that.pipe(this.value)
    parse(raw).extract[AlgorithmOutput[JValue]] match {
      case JNothing => throw new AlgorithmError(raw)
      case output => PipeIO(compact(render(output.result)), this.totalDuration+output.duration)
    }
  }
}
