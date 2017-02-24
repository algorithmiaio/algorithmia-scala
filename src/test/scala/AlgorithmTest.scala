import com.algorithmia.Algorithmia
import com.algorithmia.algo._
import org.apache.commons.codec.binary.Base64
import org.junit.Before
import org.junit.Test
import org.junit.Assume
import org.junit.Assert
import java.util.concurrent._
import scala.concurrent.duration._

class AlgorithmTest {

  private var key: String = null

  @Before
  def setup() = {
    key = System.getenv("ALGORITHMIA_API_KEY")
    Assume.assumeNotNull(key)
  }

  @Test
  def algorithmPipeJson() = {
    val res = Algorithmia.client(key).algo("docs/JavaAddOne").pipe(41)
    val output = res.map(_.result).getOrElse("")
    Assert.assertEquals("42", output)
    val result = res.as[Int]
    Assert.assertEquals(42, result)
    Assert.assertEquals(ContentType.Json, res.metadata.getContentType())
  }

  @Test
  def algorithmPipeText() = {
    val res = Algorithmia.client(key).algo("demo/Hello").pipe("foo")
    Assert.assertEquals("\"Hello foo\"", res.as(new TypeToken<JsonElement>(){}).toString())
    Assert.assertEquals("\"Hello foo\"", res.asJsonString())
    Assert.assertEquals("Hello foo", res.as(new TypeToken<String>(){}))
    Assert.assertEquals("Hello foo", res.asString())
    Assert.assertEquals(ContentType.Text, res.metadata.getContentType())
  }

  @Test
  def algorithmPipeBinary() = {
    val input = new Array[Byte](10)
    val res = Algorithmia.client(key).algo("docs/JavaBinaryInAndOut").pipe(input)
    val output = res.as[Array[Byte]]
    Assert.assertEquals(Base64.encodeBase64String(input),Base64.encodeBase64String(output))
    Assert.assertEquals(ContentType.Binary, res.metadata.getContentType())
  }

  @Test
  def algorithmRawOutput() = {
    val res = Algorithmia.client(key).algo("demo/Hello")
        .setOutputType(OutputRaw).pipe("foo")
    Assert.assertEquals("Hello foo", res.getRawOutput())
    Assert.assertEquals(null, res.metadata)
  }

  @Test
  def algorithmVoidOutput() = {
    val res = Algorithmia.client(key).algo("demo/Hello")
      .setOutputType(OutputVoid).pipe("foo")
      .getAsyncResponse()
    Assert.assertEquals("void", res.getAsyncProtocol())
    Assert.assertTrue(res.getRequestId() != null)  // request is unpredictable, but should be *something*
  }

  @Test
  def algorithmSetOption() = {
    val res = Algorithmia.client(key).algo("demo/Hello")
      .setOptions("output" -> "raw").pipe("foo")

    Assert.assertEquals("Hello foo", res.getRawOutput())
  }

  @Test
  def algorithmSetOptions() = {
    val res = Algorithmia.client(key).algo("demo/Hello")
      .setOptions("output" -> "raw").pipe("foo")

    Assert.assertEquals("Hello foo", res.getRawOutput())

  }

  @Test
  def algorithmCheckTimeout() = {
    var algo: Algorithm = Algorithmia.client(key).algo("docs/JavaAddOne")

    // Check default timeout - just for fun. This doesn't have to be specified at all time
    // but I wanted to make sure this method never throws an exception when the key in the options
    // is null.
    Assert.assertEquals(300L, algo.timeout)

    // Check Minute conversion
    algo = algo.setTimeout(20L, TimeUnit.MINUTES)
    Assert.assertEquals(20L * 60L, algo.timeout)

    // And seconds just in case
    algo = algo.setTimeout(Duration(30, SECONDS))
    Assert.assertEquals(30L, algo.timeout)

    // And milliseconds
    algo = algo.setTimeout(5000L, TimeUnit.MILLISECONDS)
    Assert.assertEquals(5L, algo.timeout)
  }
}
