package it.unibo.scafi.cobalt.ingestionService.impl

import java.util.UUID

import akka.Done
import akka.event.Logging
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Attributes
import akka.stream.scaladsl.Sink
import io.prometheus.client.{CollectorRegistry, Counter, Gauge}
import it.unibo.scafi.cobalt.common.infrastructure.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider, RabbitPublisher}
import it.unibo.scafi.cobalt.common.messages.JsonProtocol._
import it.unibo.scafi.cobalt.common.messages.{DeviceData, DeviceSensorsUpdated}
import it.unibo.scafi.cobalt.common.metrics.MetricsEndpoint
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

  val persistData: Sink[DeviceData, Future[Done]] = Sink.foreach[DeviceData](d => d.sensorsData.foreach(s =>service.updateSensorValue(d.deviceId,s._1,s._2)))

  def sensorsDataStreaming = path("deviceDataStream"){
    post{
      entity(asSourceOf[DeviceData]){ data =>
        requests.inc()

        val measurementsSubmitted = data.
        alsoTo(persistData).
        map(d => DeviceSensorsUpdated(UUID.randomUUID().toString,"DeviceSensorsUpdated",d.deviceId,d.lat,d.lon,d.sensorsData)).
        log("before-publish").
        withAttributes(Attributes.logLevels(onElement = Logging.DebugLevel)).
        alsoTo(publisher.sinkToRabbit("sensor_events", "DeviceSensorsUpdated")).
        runFold(0) { (cnt, _) => cnt + 1 }

        complete{
          measurementsSubmitted.map[ToResponseMarshallable](n => s"Processed $n metrics")
        }
      }
    }
  }


}

object IngestionApiComponent{
  type dependencies = IngestionServiceComponent with ActorSystemProvider with ExecutionContextProvider with ActorMaterializerProvider
}