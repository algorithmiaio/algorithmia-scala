package com.algorithmia.handler
import scala.reflect.runtime.universe._

import scala.util.{Success, Try}


 object AbstractAlgorithm {

   trait AbstractAlgorithm[I, O] {
     def apply(input: I): Try[O]
     def load(): Try[Unit] = Success(())
   }
 }
