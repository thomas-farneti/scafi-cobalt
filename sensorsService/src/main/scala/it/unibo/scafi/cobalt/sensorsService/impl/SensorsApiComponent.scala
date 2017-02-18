package it.unibo.scafi.cobalt.sensorsService.impl

import akka.http.scaladsl.marshalling.{Marshal, ToEntityMarshaller, ToResponseMarshallable}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import it.unibo.scafi.cobalt.sensorsService.core.SensorsServiceComponent

import scala.concurrent.ExecutionContextExecutor
import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import akka.http.javadsl.model.HttpResponse
import akka.http.scaladsl.model.ResponseEntity

/**
  * Created by tfarneti.
  */

class SensorsApiComponent(implicit val executor: ExecutionContextExecutor){ self: SensorsApiComponent.dependencies =>
  val routes: Route = {
    getSingleSensor ~ getAllSesors
  }

  private def getSingleSensor = path("device" / Segment / "sensor" / Segment){ (deviceId,sensorName) =>
    get{
      complete{
        service.getSensorValue(deviceId,sensorName).map[ToResponseMarshallable]{
          case Some(value) => value.toString
          case _ => BadRequest -> "Errore"
        }
      }
    }
  }

  private def getAllSesors = path("device" / Segment){ deviceId =>
    get{
      complete{
        service.getSensorsValues(deviceId).map[ToResponseMarshallable](_.mapValues(_.toString()))
      }
    }
  }
}

object SensorsApiComponent{
  type dependencies = SensorsServiceComponent
}