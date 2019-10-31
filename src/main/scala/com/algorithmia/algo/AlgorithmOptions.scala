package com.algorithmia.algo

/**
  * Output types: raw, void, default
  */
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

object AlgorithmOutputType {
  def fromString(outputType: String): AlgorithmOutputType = outputType match {
    case "raw" => OutputRaw
    case "void" => OutputVoid
    case _ => OutputDefault
  }
}
