import com.algorithmia.Algorithmia
import com.algorithmia.algo._
import org.apache.commons.codec.binary.Base64
import org.json4s._
import org.json4s.DefaultReaders._
import org.json4s.native.JsonMethods._
import scala.concurrent.duration._
import org.specs2.mutable._

object AlgorithmSpec extends Specification {

  private val key: String = System.getenv("ALGORITHMIA_API_KEY")

  "api key" should {
    "define environment variable ALGORITHMIA_API_KEY" in {
      key must not beNull
    }
  }

  "Algorithm.pipe(Int)" should {
    val client = Algorithmia.client(key)

    "output result 42" in {
      val res = client.algo("docs/JavaAddOne").pipe(41)
      val output: String = res.map(a => compact(render(a.result))).getOrElse("")
      output mustEqual "42"
    }

    "parse result 42" in {
      val res = client.algo("docs/JavaAddOne").pipe(41)
      val result = res.as[Int]
      result mustEqual 42
      res.metadata.content_type.content_type mustEqual ContentTypeJson.content_type
    }
  }

  "Algorithm.pipe(String)" should {
    val client = Algorithmia.client(key)
    val res = client.algo("demo/Hello").pipe("foo")

    "parse result string" in {
      res.as[String] mustEqual "Hello foo"
      res.metadata.content_type.content_type mustEqual ContentTypeText.content_type
    }
  }

  "Algorithm.pipe(ByteArray)" should {
    val input = new Array[Byte](10)
    val client = Algorithmia.client(key)
    val res = client.algo("docs/JavaBinaryInAndOut").pipe(input)

    "parse result base64" in {
      val output = res.as[String]
      input mustEqual Base64.decodeBase64(output)
      Base64.encodeBase64String(input) mustEqual output
      res.metadata.content_type.content_type mustEqual ContentTypeBinary.content_type
    }

    "parse result byte array" in {
      val output = res.as[Array[Byte]]
      input mustEqual output
      Base64.encodeBase64String(input) mustEqual Base64.encodeBase64String(output)
      res.metadata.content_type.content_type mustEqual ContentTypeBinary.content_type
    }
  }

  // @Test
  // def algorithmVoidOutput() = {
  //   val res = Algorithmia.client(key).algo("demo/Hello")
  //     .setOutputType(OutputVoid)
  //     .pipe("foo")
  //   Assert.assertEquals("void", res.getAsyncProtocol())
  //   Assert.assertTrue(res.getRequestId() != null)  // request is unpredictable, but should be *something*
  // }

  "Algorithm.setOptions" should {
    val client = Algorithmia.client(key)
    var algo = client.algo("demo/Hello")

    "set option" in {
      algo = algo.withOptions("foo" -> "bar")
      algo.options("foo") mustEqual "bar"
    }
  }

  "Algorithm.timeout" should {
    val client = Algorithmia.client(key)

    "default timeout" in {
      val algo = client.algo("docs/JavaAddOne")
      // Check default timeout - just for fun. This doesn't have to be specified at all time
      // but I wanted to make sure this method never throws an exception when the key in the options
      // is null.
      algo.timeout must beNone
    }

    "set timeout" in {
      var algo = client.algo("docs/JavaAddOne")
      // Check Minute conversion
      algo = algo.withTimeout(Duration(20, MINUTES))
      algo.timeout must beSome(Duration(20, MINUTES))
      algo.options("timeout") mustEqual "1200"

      // And seconds just in case
      algo = algo.withTimeout(Duration(30, SECONDS))
      algo.timeout must beSome(Duration(0.5, MINUTES))
      algo.options("timeout") mustEqual "30"

      // And milliseconds
      algo = algo.withTimeout(Duration(5000, MILLISECONDS))
      algo.timeout must beSome(Duration(5, SECONDS))
      algo.options("timeout") mustEqual "5"
    }
  }

}
