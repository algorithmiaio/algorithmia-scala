import com.algorithmia.Algorithmia
import com.algorithmia.algo._

import play.api.libs.json._

object Sample {

  def main(args: Array[String]) = {
    val client = Algorithmia.client(sys.env("ALGORITHMIA_API_KEY"))
    val factor = client.algo("kenny/Factor/0.1.1")
    val minmax = client.algo("codeb34v3r/FindMinMax/0.1.0")

    // Factor an number
    val factorInput = "62"
    println(s"Factoring with: $factorInput")
    val factorOutput = factor.pipe(factorInput)
    val factorResult = factorOutput.as[List[Int]]

    // Find the min and max of the factors
    val minmaxInput = Json.toJson(factorResult)
    println(s"MinMax with: $minmaxInput")
    val minmaxOutput = minmax.pipe(minmaxInput)
    val minmaxResult = minmaxOutput.as[List[Int]]

    println(s"Min and Max factors of ${factorInput}: ${minmaxResult}")
    println("----------")
    println("Repeat process with '|' syntax")

    val output = factorInput | factor | minmax
    println(s"Pipe completed in ${output.totalDuration} seconds")
    println(s"Min and Max factors of ${factorInput}: ${output.value}")
  }
}
