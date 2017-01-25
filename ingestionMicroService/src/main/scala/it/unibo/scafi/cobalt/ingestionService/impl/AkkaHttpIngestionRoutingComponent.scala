package it.unibo.scafi.cobalt.ingestionService.impl

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import it.unibo.scafi.cobalt.core.messages.ingestionService.UpdateSensorValueCmd
import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceComponent

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */

class AkkaHttpIngestionRoutingComponent(implicit val executor: ExecutionContextExecutor){ self: AkkaHttpIngestionRoutingComponent.dependencies =>
  val routes: Route = {
    path("device" / Segment / "sensor" / Segment / Segment){ (deviceId,sensorName,sensorValue) =>
      put{
        complete{
          service.updateSensorValue(UpdateSensorValueCmd(deviceId,sensorName,sensorValue)).map[ToResponseMarshallable]{
            case true => OK
            case _ => BadRequest -> "Errore"
          }
        }
      }~
      get{
        complete{
          service.updateSensorValue(UpdateSensorValueCmd(deviceId,sensorName,sensorValue)).map[ToResponseMarshallable]{
            case true => OK
            case _ => BadRequest -> "Errore"
          }
        }
      }
    }
  }
}


object AkkaHttpIngestionRoutingComponent{
  type dependencies = IngestionServiceComponent
}