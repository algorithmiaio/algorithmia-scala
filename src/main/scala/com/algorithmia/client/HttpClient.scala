package com.algorithmia.client

import java.io.InputStream
import scalaj.http._

class HttpClient(apiKey: Option[String]) {
  private val userAgent = "algorithmia-scala/1.0.0"

  // HEAD
  def head(url: String): HttpResponse[String] = {
    Http(url)
      .method("HEAD")
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .option(HttpOptions.connTimeout(60000))
      .asString
  }

  // GET
  def get(url: String): HttpResponse[String] = {
    getRequest(url).asString
  }
  def getBytes(url: String): HttpResponse[Array[Byte]] = {
    getRequest(url).asBytes
  }
  def getInputStream(url: String): HttpResponse[InputStream] = {
    getRequest(url).execute(is => is)
  }
  private def getRequest(url: String): HttpRequest = {
    Http(url)
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .option(HttpOptions.connTimeout(60000))
  }

  // POST
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

  // PUT
  def put(url: String, data: Array[Byte]): HttpResponse[String] = {
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

  // DELETE
  def delete(url: String): HttpResponse[String] = {
    Http(url)
      .method("DELETE")
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .header("Accept","application/json")
      .option(HttpOptions.connTimeout(60000))
      .asString
  }

  // PATCH
  def patch(url: String, data: String): HttpResponse[String] = {
    Http(url)
      .method("PATCH")
      .postData(data)
      .header("Content-Type", "application/json")
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .header("Accept","application/json")
      .option(HttpOptions.connTimeout(60000))
      .asString
  }

}
