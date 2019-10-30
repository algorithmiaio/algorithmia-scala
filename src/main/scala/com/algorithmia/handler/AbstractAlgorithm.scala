package com.algorithmia.handler
import play.api.libs.json._
import scala.reflect.runtime.universe._

import scala.util.{Failure, Success, Try}


 object AbstractAlgorithm {

   trait AbstractAlgorithm[-I, +O] {
     def apply(input: I): Try[O]

     def load(): Try[Unit] = Success(())
   }

   implicit class WeakTypeDetector[I: WeakTypeTag](related: AbstractAlgorithm[I, _]) {
     def getType: Type = {
       weakTypeOf[I]
     }
   }
   implicit class TypeDetector[I: TypeTag](related: AbstractAlgorithm[I, _]) {
     def getType: Type = {
       typeOf[I]
     }
   }

 }
