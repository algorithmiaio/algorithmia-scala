package com.algorithmia.algo

import com.algorithmia.{Algorithmia, AlgorithmiaClient}

class Algorithm(client: AlgorithmiaClient, algoUrl: String) {
  private val trimmedPath = algoUrl.replaceAll("^algo://|^/", "")
  val url = Algorithmia.apiBaseUrl + "/v1/algo/" + trimmedPath

  @throws(classOf[AlgorithmApiError])
  def pipe(data: String): AlgoResponse = {
    val response = client.post(url, data)

    response.code match {
      // TODO: encode to map of duration:Double and result:Encodable
      case 200 => AlgoSuccess(response.body)
      // TODO: better/userful errors
      case _ => throw new AlgorithmApiError(response.body, response.code)
    }
  }
}

case class AlgorithmOutput[T](result: T, duration: Double)

case class AlgorithmError(message: String) extends Exception(message)
case class AlgorithmApiError(body: String, code: Int) extends Exception(s"$code - $body")
