import java.io.ByteArrayInputStream

import org.specs2.mutable.Specification
import org.specs2.specification.{AfterEach, BeforeEach}
import algorithms._

import scala.io.Source

class HandlerTest extends Specification with BeforeEach with AfterEach {

  val PIPE: String = "/tmp/algoout"

  override protected def before: Any = {
    val p = Runtime.getRuntime.exec(s"touch $PIPE")
    p.waitFor
  }

  override protected def after: Any = {
    val p = Runtime.getRuntime.exec(s"rm $PIPE")
    p.waitFor()
  }

  "Basic test with String primitives" should {
    val input = """{"content_type":"text","data":"algorithmia"}"""
    val expected = """{"metadata":{"content_type":"text"},"result":"hello algorithmia"}"""
    val pipeFile = Source.fromFile(PIPE)
    System.setIn(new ByteArrayInputStream(input.getBytes(java.nio.charset.StandardCharsets.UTF_8.name)))
    BasicAlgorithm.run()
    val output = pipeFile.getLines().next()
    output mustEqual expected
  }

}
