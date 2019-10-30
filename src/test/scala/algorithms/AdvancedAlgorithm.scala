package algorithms
import com.algorithmia.Algorithmia
import com.algorithmia.handler.AbstractAlgorithm._
import play.api.libs.json.Json

import scala.util.Try

case class AlgorithmInput(foo: String)
case class AlgorithmOutput(bar: String, num: Int)

object AlgorithmInput{
  implicit val r = Json.reads[AlgorithmInput]
}
object AlgorithmOutput{
  implicit val w = Json.writes[AlgorithmOutput]
}

class AdvancedAlgorithm extends AbstractAlgorithm[AlgorithmInput, AlgorithmOutput]{

  override def apply(input: AlgorithmInput): Try[AlgorithmOutput] = Try(AlgorithmOutput( s"hello ${input.foo}", 25))
}


object AdvancedAlgorithm {
  val handler = Algorithmia.handler(new AdvancedAlgorithm)
  def main(args: Array[String]): Unit = {
    handler.serve()
  }
}