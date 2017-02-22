package com.algorithmia

import com.algorithmia.algo._
import com.algorithmia.data._
import scalaj.http._

case class AlgorithmiaClient(apiKey: Option[String]) {
  private val userAgent = "algorithmia-scala/1.0.0"
  private val baseUrl = "https://api.algorithmia.com"

  def algo(algoUrl: String): Algorithm = new Algorithm(this, algoUrl)

  def dir(dataUrl: String): DataDirectory = new DataDirectory(this, dataUrl)

  def file(dataUrl: String): DataFile = new DataFile(this, dataUrl)

  // HTTP client methods:
  def head(url: String): HttpResponse[String] = {
    Http(url)
      .method("HEAD")
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .header("Accept","application/json")
      .option(HttpOptions.connTimeout(60000))
      .asString
  }

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

  def put(url: String, data: String): HttpResponse[String] = {
    // println(s"DEBUG URL: $url")
    // println(s"DEBUG DATA: $data")
    Http(url)
      .put(data)
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .header("Accept","application/json")
      .option(HttpOptions.connTimeout(60000))
      .asString
  }

  def delete(url: String): HttpResponse[String] = {
    Http(url)
      .method("DELETE")
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .header("Accept","application/json")
      .option(HttpOptions.connTimeout(60000))
      .asString
  }

}
