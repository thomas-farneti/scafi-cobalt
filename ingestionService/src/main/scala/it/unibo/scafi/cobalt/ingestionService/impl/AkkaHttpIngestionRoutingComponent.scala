package it.unibo.scafi.cobalt.ingestionService.impl

import java.util.UUID

import akka.actor.ActorSystem
import akka.{Done, NotUsed}
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import akka.util.ByteString
import io.scalac.amqp._
import it.unibo.scafi.cobalt.common.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.core.messages.SensorData
import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceComponent
import spray.json._
import it.unibo.scafi.cobalt.core.messages.JsonProtocol._
import it.unibo.scafi.cobalt.ingestionService.TestConfig

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

/**
  * Created by tfarneti.
  */

class AkkaHttpIngestionRoutingComponent(connection : Connection) { self: AkkaHttpIngestionRoutingComponent.dependencies =>

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  val persitData: Sink[SensorData, Future[Done]] = Sink.foreach[SensorData](d => service.updateSensorValue(d.deviceId,d.sensorName,d.sensorValue))
  val rabbitMapping: Flow[SensorData, Routed, NotUsed] = Flow[SensorData].map(data => Routed( s"${data.deviceId}.${data.sensorName}", Message(body = ByteString(data.toJson.compactPrint))))

  connection.exchangeDeclare(Exchange("sensor_events", Topic, durable = true))


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
    }~
    path("sensorData"){
      post{
        entity(asSourceOf[SensorData]){data =>
          data
            .map(da => da.copy(UUID.randomUUID().toString))
            .alsoTo(persitData)
            .alsoTo(Sink.foreach(m => println(m)))
            .via(rabbitMapping)
            .runWith(Sink.fromSubscriber(connection.publish(exchange = "sensor_events")))

          complete{
            OK
          }
        }
      }
    }
  }
}


object AkkaHttpIngestionRoutingComponent{
  type dependencies = IngestionServiceComponent with ActorSystemProvider with ExecutionContextProvider with ActorMaterializerProvider
}