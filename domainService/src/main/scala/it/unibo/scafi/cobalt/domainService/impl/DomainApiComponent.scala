package it.unibo.scafi.cobalt.domainService.impl

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import io.prometheus.client.hotspot.DefaultExports
import io.prometheus.client.{CollectorRegistry, Gauge}
import it.unibo.scafi.cobalt.common.ExecutionContextProvider
import it.unibo.scafi.cobalt.common.domain.{BoundingBox, LatLon}
import it.unibo.scafi.cobalt.common.metrics.MetricsEndpoint
import it.unibo.scafi.cobalt.domainService.core.DomainServiceComponent
import spray.json.DefaultJsonProtocol

/**
  * Created by tfarneti.
  */

class DomainApiComponent extends DefaultJsonProtocol { self: DomainApiComponent.dependencies =>

  val metricsEndpoint = new MetricsEndpoint(CollectorRegistry.defaultRegistry)
  DefaultExports.initialize()

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
    }~getDevicesByBB ~metricsEndpoint.metricsRoute
  }

  def getDevicesByBB =
    path("bb") {
      parameters('lat1.as[Double], 'lon1.as[Double], 'lat2.as[Double],'lon2.as[Double]) { (lat1, lon1, lat2, lon2) =>
        val bb = BoundingBox(LatLon(lat1,lon1),LatLon(lat2,lon2))
        val devices = service.getDevicesByBoundingBox(bb)

        complete(devices)
      }
    }
}

object DomainApiComponent{
  type dependencies = DomainServiceComponent with ExecutionContextProvider
}