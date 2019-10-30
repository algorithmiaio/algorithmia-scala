package algorithms
import com.algorithmia.Algorithmia
import com.algorithmia.handler.AbstractAlgorithm._
import play.api.libs.json.Json

import scala.util.Try

case class InputFields(foo: String)
case class OutputFields(bar: String, num: Int)

object InputFields{
  implicit val r = Json.reads[InputFields]
}
object OutputFields{
  implicit val w = Json.writes[OutputFields]
}

class AdvancedAlgorithm extends AbstractAlgorithm[InputFields, OutputFields]{

  override def apply(input: InputFields): Try[OutputFields] = Try(OutputFields( s"hello ${input.foo}", 25))
}


object AdvancedAlgorithm {
  val handler = Algorithmia.handler(new AdvancedAlgorithm)
  def main(args: Array[String]): Unit = {
    handler.serve()
  }
}