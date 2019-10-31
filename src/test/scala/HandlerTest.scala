import java.io.ByteArrayInputStream

import algorithms._
import org.specs2.mutable.Specification
import org.specs2.specification.{AfterEach, BeforeEach}

import scala.io.{BufferedSource, Source}

class HandlerTest extends Specification with BeforeEach with AfterEach {

  //added this flag to ensure that all tests are run sequentially instead of in parallel
  sequential

  val PIPE: String = "/tmp/algoout"
  var pipeFile: BufferedSource = _

  override protected def before: Any = {
    val p = Runtime.getRuntime.exec(s"touch $PIPE")
    p.waitFor
    pipeFile = Source.fromFile(PIPE)
  }

  override protected def after: Any = {
    val p = Runtime.getRuntime.exec(s"rm $PIPE")
    p.waitFor()
    pipeFile.close()
  }

  "With String primitive I/O, no loading" should {
    val input = """{"content_type":"text","data":"algorithmia"}"""
    val expected = """{"metadata":{"content_type":"text"},"result":"hello algorithmia"}"""
    System.setIn(new ByteArrayInputStream(input.getBytes(java.nio.charset.StandardCharsets.UTF_8.name)))
    BasicAlgorithm.run()
    val output = pipeFile.getLines().next()
    pipeFile.close()
    output mustEqual expected
  }

  "With Custom case class I/O, with loading" should {
    val input = """{"content_type":"json","data":{"foo":"algorithmia"}}"""
    val expected = """{"metadata":{"content_type":"json"},"result":{"bar":"hello algorithmia","num":25}}"""
    System.setIn(new ByteArrayInputStream(input.getBytes(java.nio.charset.StandardCharsets.UTF_8.name)))
    AdvancedAlgorithm.run()
    val output = pipeFile.getLines().next()
    output mustEqual expected
  }

}
