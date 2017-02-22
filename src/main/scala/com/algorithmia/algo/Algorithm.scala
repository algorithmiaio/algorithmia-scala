package com.algorithmia.algo

import com.algorithmia.{Algorithmia, AlgorithmiaClient}
import scalaj.http._
import scala.util.{Either, Left, Right}
import org.json4s._
import org.json4s.native.JsonMethods._

case class Algorithm(client: AlgorithmiaClient, username: String, algoname: String, version: Version) {
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
    def apply(client: AlgorithmiaClient, algoUri: String): Algorithm = {
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
case class AlgorithmError(message: String) extends Exception(message)
case class AlgorithmApiError(body: String, code: Int) extends Exception(s"$code - $body")
