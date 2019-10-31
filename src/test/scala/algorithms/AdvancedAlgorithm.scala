package algorithms

import com.algorithmia.Algorithmia
import com.algorithmia.handler.AbstractAlgorithm
import play.api.libs.json.Json

import scala.util.Try

case class AlgorithmInput(foo: String)

case class AlgorithmOutput(bar: String, num: Int)

object AlgorithmInput {
  implicit val r = Json.reads[AlgorithmInput]
}

object AlgorithmOutput {
  implicit val w = Json.writes[AlgorithmOutput]
}

class AdvancedAlgorithm extends AbstractAlgorithm[AlgorithmInput, AlgorithmOutput] {

  var loadedValue: Option[Int] = None

  override def apply(input: AlgorithmInput): Try[AlgorithmOutput] = Try {
    val bar = s"hello ${input.foo}"
    loadedValue match {
      case Some(value) => AlgorithmOutput(bar, value)
      case None => AlgorithmOutput(bar, -1)
    }
  }

  override def load(): Try[Unit] = Try {
    loadedValue = Some(25)
  }
}


object AdvancedAlgorithm {
  def run(): Unit = {
    val handler = Algorithmia.handler(new AdvancedAlgorithm)
    handler.serve()
  }
}