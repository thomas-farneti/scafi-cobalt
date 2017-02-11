package it.unibo.scafi.cobalt.ingestionService.impl

import akka.Done
import akka.event.Logging
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Attributes
import akka.stream.scaladsl.{Flow, Sink}
import io.prometheus.client.{CollectorRegistry, Counter, Gauge}
import io.scalac.amqp._
import it.unibo.scafi.cobalt.common.messages.JsonProtocol._
import it.unibo.scafi.cobalt.common.messages.{SensorData, SensorUpdated}
import it.unibo.scafi.cobalt.common.metrics.MetricsEndpoint
import it.unibo.scafi.cobalt.common.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */

class IngestionApiComponent(publisher : RabbitPublisher) { self: IngestionApiComponent.dependencies =>

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  val metricsEndpoint = new MetricsEndpoint(CollectorRegistry.defaultRegistry)
//  DefaultExports.initialize()
  val requests: Counter = Counter.build().name("ingestion_requests_total").help("Total requests.").register()
  val connectedDevices: Gauge = Gauge.build().name("ingestion_connected_devices"). help("Connected devices").register()


  val routes: Route = putSensorData ~ sensorsDataStreaming ~ metricsEndpoint.metricsRoute


  def putSensorData = path("device" / Segment / "sensor" / Segment / Segment){ (deviceId,sensorName,sensorValue) =>
    put{
      complete{
        service.updateSensorValue(deviceId,sensorName,sensorValue).map[ToResponseMarshallable]{
          case true => Created
          case _ => BadRequest -> "Errore"
        }
      }
    }
  }

  val persistData: Sink[SensorData, Future[Done]] = Sink.foreach[SensorData](d => service.updateSensorValue(d.deviceId,d.sensorName,d.sensorValue))

  def sensorsDataStreaming = path("sensorData"){
    post{
      entity(asSourceOf[SensorData]){data =>
        requests.inc()
        connectedDevices.inc()

        data
          .alsoTo(persistData)
          .map(d => SensorUpdated(d.id,d.deviceId+"."+d.sensorName,d.deviceId,d.sensorName,d.sensorValue))
          .log("before-publish")
          .withAttributes(Attributes.logLevels(onElement = Logging.DebugLevel))
          .runWith(publisher.streamToPublisher("sensor_events", "SensorUpdated"))

        complete{
          connectedDevices.dec()
          OK
        }
      }
    }
  }


}

object IngestionApiComponent{
  type dependencies = IngestionServiceComponent with ActorSystemProvider with ExecutionContextProvider with ActorMaterializerProvider
}