package it.unibo.scafi.cobalt.networkService.impl

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import it.unibo.scafi.cobalt.networkService.core.NetworkServiceComponent

import scala.concurrent.ExecutionContextExecutor
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol

/**
  * Created by tfarneti.
  */

class AkkaHttpNetworkRoutingComponent(implicit val executor: ExecutionContextExecutor) extends DefaultJsonProtocol{
  self: AkkaHttpNetworkRoutingComponent.dependencies =>
  val routes = {
    path("nbrs" / Segment) { (deviceId) =>
      get {
        complete {
          service.getNeighbors(deviceId)
        }
      }
    } ~
    path("nbrs" / Segment / Segment) { (deviceId,nbrId) =>
      put{
        complete(Created -> service.addNeighborForDevice(deviceId,nbrId))
      }
    }~
    path("nbrs" / "spatial" / Segment){ deviceId =>
      get{
        complete(service.getNbrsSpatial(deviceId))
      }
    }

  }
}

object AkkaHttpNetworkRoutingComponent{
  type dependencies = NetworkServiceComponent
}