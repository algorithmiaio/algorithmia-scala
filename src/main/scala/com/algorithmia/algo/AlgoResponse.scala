package com.algorithmia.algo

sealed trait AlgoResponse {
  def map[T](f: AlgoSuccess => T): Option[T]
}

case class AlgoSuccess(result: String) extends AlgoResponse {
  override def map[T](f: AlgoSuccess => T): Option[T] = Some(f(this))
}

case class AlgoFailure(message: String) extends AlgoResponse {
  override def map[T](f: AlgoSuccess => T): Option[T] = None
}
