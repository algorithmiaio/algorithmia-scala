import java.io.ByteArrayInputStream

import algorithms._
import com.algorithmia.Algorithmia
import com.algorithmia.handler.AbstractAlgorithm
import org.specs2.mutable.Specification
import org.specs2.specification.{AfterEach, BeforeEach}
import play.api.libs.json._

import scala.collection.mutable.ListBuffer
import scala.io.{BufferedSource, Source}
import scala.reflect.ClassTag

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
    System.setIn(null)
  }

  def execute[I, O](input: String, algo: AbstractAlgorithm[I, O])
                   (implicit r: Reads[I], w: Writes[O], o: ClassTag[O], i: ClassTag[I]): JsValue = {
    System.setIn(new ByteArrayInputStream(input.getBytes(java.nio.charset.StandardCharsets.UTF_8.name)))
      val handler = Algorithmia.handler(algo)
      handler.serve()
    val collector = ListBuffer.empty[String]
    val lines = pipeFile.getLines()
    while(lines.hasNext){
      val next = lines.next()
      collector.append(next)
    }
    pipeFile.close()
    if(collector.length == 1){Json.parse(collector.head)}
    else Json.toJson(collector)
  }

  def duplicateRequest(input: String, num: Int): String = {
    val output = ListBuffer.empty[String]
    for(_ <- 0 until num){
      output.append(input)
    }
    output.mkString("\n")
  }

  "With String primitive I/O, no loading" should {
    val input = """{"content_type":"text","data":"algorithmia"}"""
    val expected = Json.parse("""{"metadata":{"content_type":"text"},"result":"hello algorithmia"}""")
    val output = execute(input, new Algorithm1)
    output mustEqual expected
  }

  "With Custom case class I/O, with loading" should {
    val input = """{"content_type":"json","data":{"foo":"algorithmia"}}"""
    val expected = Json.parse("""{"metadata":{"content_type":"json"},"result":{"bar":"hello algorithmia","num":25}}""")
    val output = execute(input, new Algorithm2)
    output mustEqual expected
  }

  "With failure during loading" should {
    val input = """{"content_type":"text","data":"algorithmia"}"""
    val expected = Json.parse("""{"error":{"message":"Exception: This is an expected failure state","error_type":"class java.lang.Exception"}}""")
    val output: JsValue = execute(input, new Algorithm3)
    val stacktrace = output \ "error" \"stacktrace"
    val message = output \ "error" \ "message"
    val error_type = output \ "error" \ "error_type"
    stacktrace.get.toString() must contain("java.lang.Exception: This is an expected failure state")
    message mustEqual expected \ "error" \ "message"
    error_type mustEqual expected \ "error" \ "error_type"
  }

  "With failure during runtime, with stdout loader" should {
    val input = """{"content_type":"text","data":"algorithmia"}"""
    val expected = Json.parse("""{"error":{"message":"Exception: This is another expected failure state","error_type":"class java.lang.Exception"}}""")
    val output: JsValue = execute(input, new Algorithm4)
    val stacktrace = output \ "error" \"stacktrace"
    val message = output \ "error" \ "message"
    val error_type = output \ "error" \ "error_type"
    stacktrace.get.toString() must contain("java.lang.Exception: This is another expected failure state")
    message mustEqual expected \ "error" \ "message"
    error_type mustEqual expected \ "error" \ "error_type"
  }

  //Not necessary to define a Reads and Write for List[_] when _ is primitive, but it still gets flagged by the IDE as an error
  "With list type I/O" should {
    val input = """{"content_type":"json","data":["five", "four", "three"]}"""
    val expected =  Json.parse("""{"metadata":{"content_type":"json"},"result":[4, 4, 5]}""")
    val output = execute(input, new Algorithm5)
    output mustEqual expected
  }

  "With multiple basic requests" should {
    val input = """{"content_type":"text","data":"algorithmia"}"""
    val expected = """{"metadata":{"content_type":"text"},"result":"hello algorithmia"}"""
    val dupedInput: String = duplicateRequest(input, 10)
    val dupedExpected: JsValue = Json.toJson(duplicateRequest(expected, 10).split("\n"))
    val outputs: JsValue = execute(dupedInput, new Algorithm1)
    outputs mustEqual dupedExpected
  }


}
