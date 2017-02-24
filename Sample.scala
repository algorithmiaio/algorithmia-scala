import com.algorithmia.Algorithmia
import com.algorithmia.algo._

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

object Sample {
    implicit val formats = DefaultFormats

    def main(args: Array[String]) = {
        val client = Algorithmia.client(sys.env("ALGORITHMIA_API_KEY"))
        val factor = client.algo("kenny/Factor/0.1.1")
        val minmax = client.algo("codeb34v3r/FindMinMax/0.1.0")

        // Factor an number
        val factorInput = "62"
        println(s"Factoring with: $factorInput")
        val factorOutput = factor.pipe(factorInput).map(_.result).get
        val factorResult = factorOutput.extract[AlgorithmOutput[List[Int]]]

        // Find the min and max of the factors
        val minmaxInput = compact(render(factorResult.result))
        println(s"MinMax with: $minmaxInput")
        val minmaxOutput = minmax.pipe(minmaxInput)
        val minmaxResult = factorOutput.extract[AlgorithmOutput[List[Int]]]


        println(s"Min and Max factors of ${factorInput}: ${minmaxResult.result}")
        println("----------")
        println("Repeat process with '|' syntax")


        val output = factorInput | factor | minmax
        println(s"Pipe completed in ${output.totalDuration} seconds")
        println(s"Min and Max factors of ${factorInput}: ${output.value}")
    }
}
