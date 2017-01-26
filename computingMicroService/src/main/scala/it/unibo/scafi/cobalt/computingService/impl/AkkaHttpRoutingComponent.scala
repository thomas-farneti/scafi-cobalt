package it.unibo.scafi.cobalt.computingService.impl

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingServiceComponent}

import scala.concurrent.ExecutionContextExecutor
/**
  * Created by tfarneti.
  */


class AkkaHttpRoutingComponent(implicit val executor: ExecutionContextExecutor){ self: AkkaHttpRoutingComponent.dependencies =>
  val routes: Route = {
    path("compute" / Segment){ deviceId =>
      post {
        complete {
          service.computeNewState(deviceId).map[ToResponseMarshallable] {
            case Right(s) => OK
            case Left(m) => InternalServerError -> m
          }
        }
      }
    }
  }
}


object AkkaHttpRoutingComponent{
  type dependencies = ComputingServiceComponent with CobaltBasicIncarnation
}
