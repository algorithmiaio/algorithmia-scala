package com.algorithmia.handler

import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}
class Handler[I, O](algoClass: AbstractAlgorithm[I, O]) {
  var inHandler: RequestHandler[I] = _
  var outHandler: ResponseHandler[O] = _

  protected def apply(algorithm_definition: AbstractAlgorithm[I, O]): Handler[I, O] = {
    val input_type = getInputType(algorithm_definition.apply)
    inHandler = RequestHandler[I](input_type)
    outHandler = ResponseHandler[O]()
    new Handler(algorithm_definition)
  }

  private def getInputType(ab: I => Try[O])(implicit tag: WeakTypeTag[I]): Type = {
    val targs = tag.tpe match { case TypeRef(_, _, args) => args }
    targs(0)
  }

  private def load(): Try[Unit] = {
      algoClass.load().map(_ => {
        System.out.println("PIPE_INIT_COMPLETE")
        System.out.flush()
      })
  }


  def serve(): Unit = {
    val lines = io.Source.stdin.getLines()
    load() match {
      case Failure(exception) => outHandler.writeErrorToPipe(exception)
      case Success(_) =>
        for (line <- lines) {
          inHandler.processRequest(line)(algoClass.inputReader)
            .flatMap(i => algoClass.apply(i))
            .map(o => outHandler.writeResponseToPipe(o)(algoClass.outputWriter))
        }
    }
  }

}

