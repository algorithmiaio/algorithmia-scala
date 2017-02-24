package com.algorithmia.algo

import com.algorithmia.{Algorithmia, AlgorithmiaClient}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Json
import scala.concurrent.duration.{Duration, FiniteDuration, SECONDS}

class Algorithm(client: AlgorithmiaClient, algoUrl: String, options: Map[String,String] = Map.empty) {
  private val trimmedPath: String = algoUrl.replaceAll("^algo://|^/", "")
  val url: String = Algorithmia.apiBaseUrl + "/v1/algo/" + trimmedPath

  @throws(classOf[AlgorithmApiError])
  def pipe(input: Any): AlgoResponse = {
    val inputJson = new Json(DefaultFormats).decompose(input)
    val response = client.http.post(url, inputJson.toString)

    val metadata = ??? // TODO
    response.code match {
      // TODO: encode to map of duration:Double and result:Encodable
      case 200 => AlgoSuccess(parse(response.body), metadata)
      // TODO: better/userful errors
      case _ => AlgoFailure(response.body, metadata)
    }
  }

  def setOptions(opts: (String,String)*): Algorithm = {
    new Algorithm(client, algoUrl, options ++ opts)
  }

  def timeout: Option[FiniteDuration] = {
    options.get("timeout").map(sec => Duration(sec.toLong, SECONDS))
  }
  def setTimeout(timeout: FiniteDuration): Algorithm = {
    setOptions("timeout" -> timeout.toSeconds.toString)
  }

  def setOutputType(outputType: AlgorithmOutputType): Algorithm = {
    setOptions("output" -> outputType.parameter)
  }

}

case class AlgorithmOutput[T](result: T, metadata: Metadata)

class AlgorithmError(message: String) extends Exception(message)
class AlgorithmApiError(body: String, code: Int) extends Exception(s"$code - $body")
