package it.unibo.scafi.cobalt.ingestionService.impl

import akka.{Done, NotUsed}
import spray.json._
import akka.actor.ActorSystem
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import it.unibo.scafi.cobalt.core.messages.SensorData
import it.unibo.scafi.cobalt.core.messages.ingestionService.UpdateSensorValueCmd
import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceComponent
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by tfarneti.
  */

object JsonProtocol
  extends akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
    with spray.json.DefaultJsonProtocol {

  implicit val sensorDataFormat = jsonFormat4(SensorData.apply)
}

class AkkaHttpIngestionRoutingComponent(producerSettings : ProducerSettings[Array[Byte], String])(implicit val executor: ExecutionContextExecutor){ self: AkkaHttpIngestionRoutingComponent.dependencies =>

  import JsonProtocol._
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  val persitData: Sink[SensorData, Future[Done]] = Sink.foreach[SensorData](d => service.updateSensorValue(d.deviceId,d.sensorName,d.sensorValue))

  val convertToKafkaRecord = Flow[SensorData].map{ elem =>
    new ProducerRecord[Array[Byte],String]("sensorData", elem.toJson.toString())
  }

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
            .alsoTo(persitData)
            .via(convertToKafkaRecord)
            .to(Producer.plainSink(producerSettings))
          complete{
            Created
          }
        }
      }
    }
  }
}


object AkkaHttpIngestionRoutingComponent{
  type dependencies = IngestionServiceComponent
}