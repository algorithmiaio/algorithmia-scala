package com.algorithmia

import com.algorithmia.algo._
import scalaj.http._

case class AlgorithmiaClient(apiKey: Option[String]) {
  private val userAgent = s"scala algorithmia.scala"
  private val baseUrl = s"https://api.algorithmia.com"

  def algo(algoUri: String): Algorithm = Algorithm(this, algoUri)

  def post(url: String, data: String): HttpResponse[String] = {
    // println(s"DEBUG URL: $url")
    // println(s"DEBUG DATA: $data")
    Http(url)
      .postData(data)
      .header("Content-Type", "application/json")
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .header("Accept","application/json")
      .option(HttpOptions.connTimeout(60000))
      .asString
  }
}
