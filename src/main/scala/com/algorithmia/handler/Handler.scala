package com.algorithmia.handler

import com.algorithmia.handler.AbstractAlgorithm._
import play.api.libs.json.{Reads, Writes}

import scala.reflect.runtime.universe._
import scala.util.{Failure, Success, Try}
case class Handler[I: WeakTypeTag, O](algorithm: AbstractAlgorithm[I, O]) {

  val requestHandler: RequestHandler[I] = RequestHandler(algorithm.getType)
  val responseHandler: ResponseHandler[O] = ResponseHandler()


  private def load(): Try[Unit] = {
    algorithm.load().map(_ => {
        System.out.println("PIPE_INIT_COMPLETE")
        System.out.flush()
      })
  }


  def serve()(implicit r: Reads[I], w: Writes[O]): Unit = {
    val lines = io.Source.stdin.getLines()
    load() match {
      case Failure(exception) => responseHandler.writeErrorToPipe(exception)
      case Success(_) =>
        for (line <- lines) {
          requestHandler.processRequest(line)
            .flatMap(i => algorithm.apply(i))
            .map(o => responseHandler.writeResponseToPipe(o))
        }
    }
  }

}