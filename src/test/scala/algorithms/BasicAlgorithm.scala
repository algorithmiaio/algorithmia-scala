package algorithms

import com.algorithmia.Algorithmia
import com.algorithmia.handler.AbstractAlgorithm.AbstractAlgorithm

import scala.util.Try

class BasicAlgorithm extends AbstractAlgorithm[String, String]{

  override def apply(input: String): Try[String] = Try(s"hello ${input}")
}


object BasicAlgorithm {
  val handler = Algorithmia.handler(new BasicAlgorithm)
  def main(args: Array[String]): Unit = {
    handler.serve()
  }
}