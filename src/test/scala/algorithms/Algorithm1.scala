package algorithms

import com.algorithmia.handler.AbstractAlgorithm

import scala.util.Try

class Algorithm1 extends AbstractAlgorithm[String, String] {

  override def apply(input: String): Try[String] = Try(s"hello ${input}")
}
