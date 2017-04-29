import com.algorithmia.algo._
import org.apache.commons.io.IOUtils
import org.json4s.JValue
import org.json4s.DefaultReaders._
import org.specs2.mutable._

object AlgoResponseTest extends Specification {

  "algorithm response" should {

    "handle failure" in {
      val response = parseResourceAsResponse("algo_failure.json")
      response.isSuccess must beFalse
    }

    "handle success" in {
      val response = parseResourceAsResponse("algo_success_json_array_long.json")
      response.isSuccess must beTrue
      response.as[JValue].toString mustEqual "[2,2,2,3,3]"
      response.asJsonString mustEqual "[2,2,2,3,3]"
    }

  }

  "algorithm response output" should {

    "handle string" in {
      val response = parseResourceAsResponse("algo_success_text_string.json")
      val result = response.as[String]
      val expected = "This is a success test"
      expected mustEqual result
    }

    "handle string to int" in {
      val response = parseResourceAsResponse("algo_success_text_int.json")
      val result = response.as[Int]
      result mustEqual 42
    }

    "handle int" in {
      val response = parseResourceAsResponse("algo_success_json_int.json")
      val result = response.as[Int]
      result mustEqual 42
    }

    "handle int to string" in {
      val response = parseResourceAsResponse("algo_success_json_int.json")
      val result = response.as[String]
      result mustEqual "42"
    }

    "handle int list" in {
      val response = parseResourceAsResponse("algo_success_json_array_long.json")
      val result = response.as[List[Int]]
      val expected = List(2, 2, 2, 3, 3)
      expected mustEqual result
    }

  }

  "algorithm response output void" should {

    "handle success" in {
      val response = parseResourceAsResponse("algo_success_void.json")
      response.isSuccess must beTrue
      val result = response.asOpt[Int]
      result must beNone
    }

  }

  "algorithm response output binary" should {

    "handle success" in {
      val response = parseResourceAsResponse("algo_success_binary.json")
      response.isSuccess must beTrue
      response.metadata.content_type mustEqual ContentTypeBinary
      val result = response.as[Array[Byte]]
      result.length mustEqual 10
    }

  }

  "algorithm response metadata" should {

    "parse content type" in {
      val response = parseResourceAsResponse("algo_success_json_array_long.json")
      response.metadata.content_type mustEqual ContentTypeJson
    }

    "parse duration" in {
      val response = parseResourceAsResponse("algo_success_json_array_long.json")
      response.metadata.duration must beCloseTo(0.035916637, 0.0001)
    }

  }

  // Helpers
  private def parseResourceAsResponse(filename: String) = {
    val is = this.getClass.getResourceAsStream(filename)
    val rawOutput = IOUtils.toString(is, "UTF-8")
    AlgoResponse(rawOutput, OutputDefault)
  }

}
