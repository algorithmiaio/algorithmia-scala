package com.algorithmia {

  package object algo {
    import scala.language.implicitConversions

    /** Implicitly convert string to PipeIO */
    implicit def stringtoPipeIO(value: String): PipeIO = PipeIO(value)

  }

}
