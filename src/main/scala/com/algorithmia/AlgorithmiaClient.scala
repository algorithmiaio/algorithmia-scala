package com.algorithmia

import com.algorithmia.algo._
import com.algorithmia.client.HttpClient
import com.algorithmia.data._

case class AlgorithmiaClient(apiKey: Option[String]) {
  val http = new HttpClient(apiKey)

  def algo(algoUrl: String): Algorithm = new Algorithm(this, algoUrl)

  def dir(dataUrl: String): DataDirectory = new DataDirectory(this, dataUrl)

  def file(dataUrl: String): DataFile = new DataFile(this, dataUrl)

  def report_insights(input: String): AlgorithmiaInsights = new AlgorithmiaInsights(this, input)

}