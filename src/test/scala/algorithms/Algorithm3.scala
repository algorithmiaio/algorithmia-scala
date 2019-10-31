package algorithms

import com.algorithmia.handler.AbstractAlgorithm

import scala.util.Try

class Algorithm3 extends AbstractAlgorithm[Int, String]{
  override def apply(input: Int): Try[String] = Try{
  s"the number is ${input}"
  }

  override def load(): Try[Unit] = Try {
    throw new Exception("This is an expected failure state")
  }
}