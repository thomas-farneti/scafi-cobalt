package it.unibo.scafi.cobalt.computingService.impl

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.Route
import it.unibo.scafi.cobalt.core.messages.computingService.{ComputeNewStateCommand, ComputeNewStateResponse}
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import it.unibo.scafi.cobalt.computingService.ComputingServiceComponent

import scala.concurrent.ExecutionContextExecutor
import scala.util.Failure
/**
  * Created by tfarneti.
  */

trait Protocols extends DefaultJsonProtocol{
  implicit val ComputeNewStateCommandFormat = jsonFormat1(ComputeNewStateCommand.apply)
  implicit val ComputeNewStateResponseFormat = jsonFormat1(ComputeNewStateResponse.apply)
}

class AkkaHttpRoutingComponent(implicit val executor: ExecutionContextExecutor){ self: AkkaHttpRoutingComponent.dependencies =>
  val routes: Route = {
    path("compute"){
      (post & entity(as[ComputeNewStateCommand])){ cmd=>
        complete{
          service.computeNewState(cmd).map[ToResponseMarshallable]{
            case Right(state) => ComputeNewStateResponse(state.id)
            case Left(message) => InternalServerError -> message
          }
        }
      }~
      (post & entity(as[ComputeNewStateCommand])){ cmd =>
        complete(OK)
      }
    }
  }
}

object AkkaHttpRoutingComponent{
  type dependencies = ComputingServiceComponent with Protocols
}
