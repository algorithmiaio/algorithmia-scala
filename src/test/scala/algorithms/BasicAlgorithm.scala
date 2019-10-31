package algorithms

import com.algorithmia.Algorithmia
import com.algorithmia.handler.AbstractAlgorithm

import scala.util.Try

class BasicAlgorithm extends AbstractAlgorithm[String, String]{

  override def apply(input: String): Try[String] = Try(s"hello ${input}")
}


object BasicAlgorithm {
  def run(): Unit = {
    val handler = Algorithmia.handler(new BasicAlgorithm)
    handler.serve()
  }
}