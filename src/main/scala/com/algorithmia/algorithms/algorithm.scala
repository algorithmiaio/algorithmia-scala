package algorithmia.algorithms

import algorithmia.{Algorithmia,Client}
import scalaj.http._
import scala.util.{Either, Left, Right}
import org.json4s._
import org.json4s.native.JsonMethods._

case class PipeIO(value: String, totalDuration: Double = 0) {
    implicit val formats = DefaultFormats

    @throws(classOf[AlgorithmApiError])
    def |(that: Algorithm): PipeIO = {
        val raw = that.pipe(this.value)
        val output = parse(raw).extract[AlgorithmOutput[JValue]]
        PipeIO(compact(render(output.result)), this.totalDuration+output.duration)
    }
}

case class Algorithm(client: Client, username: String, algoname: String, version: Version) {
    val algorithmBaseUrl = s"${Algorithmia.apiBaseUrl}/api"

    def toUrl: String = version match {
        case Version.Latest => s"$algorithmBaseUrl/$username/$algoname"
        case _ => s"$algorithmBaseUrl/$username/$algoname/${version.toString}"
    }

    @throws(classOf[AlgorithmApiError])
    def pipe(data: String): String = {
        val response = client.post(this.toUrl, data)

        response.code match {
            // TODO: encode to map of duration:Double and result:Encodable
            case 200 => response.body
            // TODO: better/userful errors
            case _ => throw new AlgorithmApiError(response.body, response.code)
        }
    }
}

object Algorithm {

    @throws(classOf[AlgorithmParseException])
    @throws(classOf[VersionParseException])
    def apply(client: Client, algoUri: String): Algorithm = {
        // TODO: strip algo:// prefix
        val (username, algoname, versionString) = algoUri.split("/") match {
            case Array(user, algo, ver) => (user, algo, ver)
            case Array(user, algo) => (user, algo, "latest")
            case _ => throw new AlgorithmParseException(algoUri)
        }
        Algorithm(client, username, algoname, Version.fromString(versionString))
    }
}

case class AlgorithmOutput[T](result: T, duration: Double)

case class AlgorithmParseException(algoUri :String) extends Exception(algoUri)
case class AlgorithmApiError(body: String, code: Int) extends Exception(s"$code - $body")
