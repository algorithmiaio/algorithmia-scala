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
    val httpResponse = client.http.post(url, inputJson.toString)

    httpResponse.code match {
      case 200 => AlgoResponse.fromJson(parse(httpResponse.body))
      case _ => AlgoFailure(httpResponse.body, Metadata(0, ContentTypeVoid.content_type, None))
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
