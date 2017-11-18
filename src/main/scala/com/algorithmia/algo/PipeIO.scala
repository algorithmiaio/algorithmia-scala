package com.algorithmia.algo

/**
 * PipeIO uses scala magic to enable unix-style piping to algorithms.
 * input | algo1 | algo2
 */
case class PipeIO(value: String, totalDuration: Double = 0) {

  def |(that: Algorithm): PipeIO = {
    that.pipe(this.value) match {
      case AlgoSuccess(result, metadata, _) => {
        PipeIO(result.toString, this.totalDuration + metadata.duration)
      }
      case AlgoFailure(message, metadata, _) => throw new AlgorithmError(message)
    }
  }
}

private class AlgorithmError(message: String) extends Exception(message)
