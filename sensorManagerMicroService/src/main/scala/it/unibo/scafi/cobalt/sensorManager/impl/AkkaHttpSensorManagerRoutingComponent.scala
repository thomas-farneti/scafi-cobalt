package it.unibo.scafi.cobalt.sensorManager.impl

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import it.unibo.scafi.cobalt.sensorManager.core.SensorManagerServiceComponent

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */

class AkkaHttpSensorManagerRoutingComponent(implicit val executor: ExecutionContextExecutor){ self: AkkaHttpSensorManagerRoutingComponent.dependencies =>
  val routes: Route = {
    path("device" / Segment / "sensor" / Segment){ (deviceId,sensorName) =>
      get{
        complete{
          service.getSensorValue(deviceId,sensorName).map[ToResponseMarshallable]{
            case Some(value) => value
            case _ => BadRequest -> "Errore"
          }
        }
      }
    }
  }
}

object AkkaHttpSensorManagerRoutingComponent{
  type dependencies = SensorManagerServiceComponent
}