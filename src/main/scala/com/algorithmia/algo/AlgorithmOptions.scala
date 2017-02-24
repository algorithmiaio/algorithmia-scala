package com.algorithmia.algo

sealed trait AlgorithmOutputType {
  def parameter: String
}

case object OutputRaw extends AlgorithmOutputType {
  override def parameter = "raw"
}
case object OutputVoid extends AlgorithmOutputType {
  override def parameter = "void"
}
case object OutputDefault extends AlgorithmOutputType {
  override def parameter = ""
}
