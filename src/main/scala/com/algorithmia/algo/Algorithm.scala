package com.algorithmia.algo

import com.algorithmia.{Algorithmia, AlgorithmiaClient}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Json
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{Duration, FiniteDuration, SECONDS}

class Algorithm(client: AlgorithmiaClient, algoUrl: String, val options: Map[String,String] = Map.empty) {
  private val trimmedPath: String = algoUrl.replaceAll("^algo://|^/", "")
  val url: String = Algorithmia.apiBaseUrl + "/v1/algo/" + trimmedPath

  @throws(classOf[AlgorithmApiError])
  def pipe(input: Any): AlgoResponse = {
    val inputJson = new Json(DefaultFormats).decompose(input)
    val httpResponse = client.http.post(url, inputJson.toString)
    val rawOutput = httpResponse.body

    httpResponse.code match {
      case 200 => AlgoResponse(rawOutput, outputType)
      case _ => AlgoFailure(rawOutput, Metadata(0, ContentTypeVoid, None), rawOutput)
    }
  }

  /**
   * Set algorithm options, to be passed into algorithmia as query parameters.
   * We use "with" instead of "set" because the Algorithm object is immutable.
   */
  def withOptions(opts: (String,String)*): Algorithm = {
    new Algorithm(client, algoUrl, options ++ opts)
  }

  def timeout: Option[FiniteDuration] = {
    options.get("timeout").map(sec => Duration(sec.toLong, SECONDS))
  }
  def withTimeout(timeout: FiniteDuration): Algorithm = {
    withOptions("timeout" -> timeout.toSeconds.toString)
  }

  def outputType: AlgorithmOutputType = {
    options.get("output").map(AlgorithmOutputType.fromString).getOrElse(OutputDefault)
  }
  def withOutputType(outputType: AlgorithmOutputType): Algorithm = {
    withOptions("output" -> outputType.parameter)
  }

}

case class AlgorithmOutput[T](result: T, metadata: Metadata)

class AlgorithmError(message: String) extends Exception(message)
class AlgorithmApiError(body: String, code: Int) extends Exception(s"$code - $body")
