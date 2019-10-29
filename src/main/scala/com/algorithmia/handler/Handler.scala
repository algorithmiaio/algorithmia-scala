package com.algorithmia.handler

import scala.reflect.runtime.universe._

class Handler[I, O](algorithm_definition: AbstractAlgorithm[I, O]) {
  var inHandler: RequestHandler[I]
  var outHandler: ResponseHandler

  def apply(algorithm_definition: AbstractAlgorithm[I, O]): Handler[I, O] = {
    val input_type = getInputType(algorithm_definition.apply)
    inHandler = RequestHandler[I](input_type)
    outHandler = ResponseHandler()
    new Handler(algorithm_definition)
  }

  def getInputType(ab: I => O)(implicit tag: TypeTag[I]): Type = {
    val targs = tag.tpe match { case TypeRef(_, _, args) => args }
    targs(0)
  }

  def load(): Either[Throwable, Unit] = {
      algorithm_definition.load() match {
        case Right(_) =>
          System.out.println("PIPE_INIT_COMPLETE")
          System.out.flush()
          Right()
        case Left(err) => Left(err)
      }
  }

  def serve(): Unit = {
  val lines = io.Source.stdin.getLines()
    load().map(_ => outHandler.writeErrorToPipe(_))
    for(line <- lines){
      val input: I = inHandler.processRequest(line)(algorithm_definition.inputReader).map(_ => outHandler.writeErrorToPipe(_))
      val output: O = algorithm_definition.apply(input).map(_ => outHandler.writeErrorToPipe(_))
      outHandler.writeResponseToPipe(algorithm_definition.outputWriter)(output).map(_ => outHandler.writeErrorToPipe(_))
    }
  }

}

