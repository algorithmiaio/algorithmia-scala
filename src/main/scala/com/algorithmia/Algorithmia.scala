package com.algorithmia

import com.algorithmia.algo._
import com.algorithmia.data._

object Algorithmia {

  private val defaultClient: AlgorithmiaClient = AlgorithmiaClient(None)

  def client(simpleKey: String): AlgorithmiaClient = AlgorithmiaClient(Some(simpleKey))
  def client(simpleKeyOpt: Option[String] = None): AlgorithmiaClient = AlgorithmiaClient(simpleKeyOpt)

  // Helper methods using the default client:
  def algo(algoUrl: String): Algorithm = defaultClient.algo(algoUrl)
  def dir(dataUrl: String): DataDirectory = defaultClient.dir(dataUrl)
  def file(dataUrl: String): DataFile = defaultClient.file(dataUrl)

}
