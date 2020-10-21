import AlgorithmTest.key
import com.algorithmia.Algorithmia
import com.algorithmia.algo.AlgorithmiaInsights
import org.scalatest.{FlatSpec, MustMatchers}
import org.specs2.mutable._

object AlgorithmiaInsightsSpec extends FlatSpec with MustMatchers {
 val keys: String = System.getenv("ALGORITHMIA_API_KEY")
 val client = Algorithmia.client(keys)
 val algo = new AlgorithmiaInsights(client, "")
 "api key" should
   "define environment variable ALGORITHMIA_API_KEY" in {
  key must not be null
 }


 "AlgorithmiaInsight Calling status" should "be false " in {
   algo.report_insight().isSuccess must be
  false

 }
val algo2 = new AlgorithmiaInsights(client, """{"cats_in_image": 4, "dogs_in_image": 7}""")
 "Algorithmia Client" should "give  status" in {

  algo2.report_insight().isSuccess must be
  true


 }
}