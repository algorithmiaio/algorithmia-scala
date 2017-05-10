package com.algorithmia.client

import java.io.{InputStream, OutputStream}
import java.net.HttpURLConnection

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
  def getInputStream[T](url: String)(op: InputStream => T): HttpResponse[T] = {
    getRequest(url).execute(op)
  }
  private def getRequest(url: String): HttpRequest = {
    Http(url)
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .option(HttpOptions.connTimeout(60000))
  }

  // POST
  def post(url: String, data: String): HttpResponse[String] = {
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
    Http(url)
      .put(data)
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .header("Accept","application/json")
      .option(HttpOptions.connTimeout(60000))
      .asString
  }

  def put(url: String, is: InputStream, length: Option[Long]): HttpResponse[String] = {
    Http(url)
      .method("PUT")
      .options({ conn =>
        conn.setDoOutput(true)
        length.foreach(l => conn.setFixedLengthStreamingMode(l))
        val os = conn.getOutputStream
        copy(is, os)
      })
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
      .postData(data)
      .method("PATCH")
      .header("Content-Type", "application/json")
      .header("User-Agent", userAgent)
      .header("Authorization", apiKey.orNull)
      .header("Accept","application/json")
      .option(HttpOptions.connTimeout(60000))
      .asString
  }

  private def copy(is: InputStream, os: OutputStream): Unit = {
    val bytes = new Array[Byte](1024) //1024 bytes - Buffer size
    Iterator
      .continually (is.read(bytes))
      .takeWhile (_ != -1)
      .foreach (read => os.write(bytes,0,read))
    os.close()
  }

}
