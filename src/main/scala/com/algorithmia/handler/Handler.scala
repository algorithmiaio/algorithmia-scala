package com.algorithmia.handler

import com.algorithmia.handler.AbstractAlgorithm._
import play.api.libs.json.{Reads, Writes}

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}
case class Handler[I, O](algorithm: AbstractAlgorithm[I, O]) {

  val requestHandler: RequestHandler[I] = RequestHandler()
  val responseHandler: ResponseHandler[O] = ResponseHandler()


  private def load(): Try[Unit] = {
    algorithm.load().map(_ => {
        System.out.println("PIPE_INIT_COMPLETE")
        System.out.flush()
      })
  }


  def serve()(implicit r: Reads[I], w: Writes[O], o: ClassTag[O], i: ClassTag[I]): Unit = {
    val lines = io.Source.stdin.getLines()
    load() match {
      case Failure(exception) => responseHandler.writeErrorToPipe(exception)
      case Success(_) =>
        for (line <- lines) {
          requestHandler.processRequest(line)
            .flatMap(i => algorithm.apply(i))
            .map(output => responseHandler.writeResponseToPipe(output))
        }
    }
  }

}