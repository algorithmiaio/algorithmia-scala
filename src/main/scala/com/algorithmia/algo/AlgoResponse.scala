package com.algorithmia.algo

import org.json4s.JValue

sealed trait AlgoResponse {
  def map[T](f: AlgoSuccess => T): Option[T]
  def metadata: Metadata
}

case class AlgoSuccess(result: JValue, metadata: Metadata) extends AlgoResponse {
  override def map[T](f: AlgoSuccess => T): Option[T] = Some(f(this))
}

case class AlgoFailure(message: String, metadata: Metadata) extends AlgoResponse {
  override def map[T](f: AlgoSuccess => T): Option[T] = None
}
