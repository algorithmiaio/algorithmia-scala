package algorithms

import com.algorithmia.handler.AbstractAlgorithm

import scala.util.Try

class Algorithm4 extends AbstractAlgorithm[String, String]{

  override def apply(input: String): Try[String] = Try{
    throw new Exception("This is another expected failure state")
  }

  override def load(): Try[Unit] = Try{println("foo")}

}
