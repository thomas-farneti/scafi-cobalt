package it.unibo.scafi.cobalt.ingestionService.impl

import java.util.UUID

import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink}
import akka.util.ByteString
import akka.{Done, NotUsed}
import io.prometheus.client.{CollectorRegistry, Counter, Gauge}
import io.prometheus.client.hotspot.DefaultExports
import io.scalac.amqp._
import it.unibo.scafi.cobalt.common.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.common.messages.JsonProtocol._
import it.unibo.scafi.cobalt.common.messages.SensorData
import it.unibo.scafi.cobalt.common.metrics.MetricsEndpoint
import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceComponent
import spray.json._

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */

class IngestionApiComponent(connection : Connection) { self: IngestionApiComponent.dependencies =>

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  val persitData: Sink[SensorData, Future[Done]] = Sink.foreach[SensorData](d => service.updateSensorValue(d.deviceId,d.sensorName,d.sensorValue))
  val rabbitMapping: Flow[SensorData, Routed, NotUsed] = Flow[SensorData].map(data => Routed( s"${data.deviceId}.${data.sensorName}", Message(body = ByteString(data.toJson.compactPrint))))

  connection.exchangeDeclare(Exchange("sensor_events", Topic, durable = true))

  val metricsEndpoint = new MetricsEndpoint(CollectorRegistry.defaultRegistry)
  DefaultExports.initialize()

  val requests: Counter = Counter.build()
    .name("requests_total").help("Total requests.").register()
  val connectedDevices: Gauge = Gauge.build()
    .name("connected_devices"). help("Connected devices").register()


  val routes: Route = {
    path("device" / Segment / "sensor" / Segment / Segment){ (deviceId,sensorName,sensorValue) =>
      put{
        complete{
          service.updateSensorValue(deviceId,sensorName,sensorValue).map[ToResponseMarshallable]{
            case true => Created
            case _ => BadRequest -> "Errore"
          }
        }
      }
    }~ sensorsData ~ metricsEndpoint.metricsRoute

  }

  def sensorsData = path("sensorData"){
    post{
      entity(asSourceOf[SensorData]){data =>
        requests.inc()
        connectedDevices.inc()
        data
          .map(da => da.copy(UUID.randomUUID().toString))
          .alsoTo(persitData)
          .alsoTo(Sink.foreach(m => println(m)))
          .via(rabbitMapping)
          .runWith(Sink.fromSubscriber(connection.publish(exchange = "sensor_events")))

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