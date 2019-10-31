package com.algorithmia.handler

import scala.util.{Success, Try}

   trait AbstractAlgorithm[I, O] {
     def apply(input: I): Try[O]
     def load(): Try[Unit] = Success(())
 }
