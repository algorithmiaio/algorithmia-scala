package com.algorithmia.client

import java.io.{InputStream, OutputStream}
import java.net.HttpURLConnection

import scalaj.http._

class HttpClient(apiKey: Option[String]) {
  private val userAgent = "algorithmia-scala/1.0.0"

  private val connTimeout = 30 * 1000 // 30sec
  private val readTimeout = 3060 * 1000 // 51min

  private def httpRequest(url: String): HttpRequest = {
    val headers = Seq("User-Agent" -> userAgent) ++ apiKey.map("Authorization" -> _)
    Http(url)
      .headers(headers)
      .timeout(connTimeout, readTimeout)
  }

  // HEAD
  def head(url: String): HttpResponse[String] = {
    httpRequest(url)
      .method("HEAD")
      .asString
  }

  // GET
  def get(url: String): HttpResponse[String] = {
    httpRequest(url).asString
  }
  def getBytes(url: String): HttpResponse[Array[Byte]] = {
    httpRequest(url).asBytes
  }
  def getInputStream[T](url: String)(op: InputStream => T): HttpResponse[T] = {
    httpRequest(url).execute(op)
  }

  // POST
  def post(url: String, data: String): HttpResponse[String] = {
    httpRequest(url)
      .postData(data)
      .header("Content-Type", "application/json")
      .header("Accept","application/json")
      .asString
  }

  // PUT
  def put(url: String, data: Array[Byte]): HttpResponse[String] = {
    httpRequest(url)
      .put(data)
      .header("Accept","application/json")
      .asString
  }

  def put(url: String, is: InputStream, length: Option[Long]): HttpResponse[String] = {
    httpRequest(url)
      .method("PUT")
      .options({ conn =>
        conn.setDoOutput(true)
        length.foreach(l => conn.setFixedLengthStreamingMode(l))
        val os = conn.getOutputStream
        copy(is, os)
      })
      .header("Accept","application/json")
      .asString
  }

  // DELETE
  def delete(url: String): HttpResponse[String] = {
    httpRequest(url)
      .method("DELETE")
      .header("Accept","application/json")
      .asString
  }

  // PATCH
  def patch(url: String, data: String): HttpResponse[String] = {
    httpRequest(url)
      .postData(data)
      .method("PATCH")
      .header("Content-Type", "application/json")
      .header("Accept","application/json")
      .asString
  }

  /**
   * Use our own stream copy instead of adding dependency on commons io
   */
  private def copy(is: InputStream, os: OutputStream): Unit = {
    val buffer = new Array[Byte](1024)
    var len = is.read(buffer)
    while( len != -1 ) {
      os.write(buffer, 0, len)
      len = is.read(buffer)
    }
    is.close()
    os.close()
  }

}
