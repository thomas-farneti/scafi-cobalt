package it.unibo.scafi.cobalt.domainService.impl

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import it.unibo.scafi.cobalt.domainService.core.DomainServiceComponent
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */

class HttpDomainComponent(implicit val executor: ExecutionContextExecutor) extends DefaultJsonProtocol{
  self: HttpDomainComponent.dependencies =>
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

object HttpDomainComponent{
  type dependencies = DomainServiceComponent
}