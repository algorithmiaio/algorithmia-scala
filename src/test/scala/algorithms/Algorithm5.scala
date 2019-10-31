package algorithms

import com.algorithmia.handler.AbstractAlgorithm

import scala.util.Try

class Algorithm5 extends AbstractAlgorithm[List[String], List[Int]] {
  override def apply(input: List[String]): Try[List[Int]] = Try {
    input.map(s => s.length)
  }
}
