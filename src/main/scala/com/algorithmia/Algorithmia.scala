package com.algorithmia

import com.algorithmia.algo._

object Algorithmia {
  val apiBaseUrl: String = "https://api.algorithmia.com"

  private val defaultClient: AlgorithmiaClient = AlgorithmiaClient(None)

  def client(simpleKey: String): AlgorithmiaClient = AlgorithmiaClient(Some(simpleKey))

  def algo(algoUri: String): Algorithm = Algorithm(defaultClient, algoUri)

}
