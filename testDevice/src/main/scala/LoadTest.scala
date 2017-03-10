import java.nio.file.{Path, Paths}
import java.time.LocalDateTime
import java.util.UUID

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.javadsl.model.HttpEntities
import akka.http.javadsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FileIO, Framing, Source}
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import io.scalac.amqp._
import it.unibo.scafi.cobalt.common.infrastructure.RabbitPublisher
import it.unibo.scafi.cobalt.common.messages.JsonProtocol._
import it.unibo.scafi.cobalt.common.messages.{DeviceData, DeviceSensorsUpdated}

import scala.concurrent.duration._
import scala.util.Failure


//object LoadTest extends App {
//  implicit val system = ActorSystem()
//  implicit def ec = system.dispatcher
//  implicit val materializer = ActorMaterializer()
//
//
//  val pool = Http().cachedHostConnectionPool[DeviceData]("ingestion", 8080)
//
//  var cont = 0
//
//
//  Source.repeat(1)
//    .mapAsync(8) { l =>
//
//      val data = DeviceData(UUID.randomUUID().toString, cont.toString, 44.1391, 12.24315 , Map("source" -> (cont == 0).toString))
//      cont = (cont + 1) % 1000
//
//      Marshal(data).to[RequestEntity].map { e =>
//        HttpRequest(method = HttpMethods.POST, uri = Uri("/deviceDataStream"), entity = e) -> data
//      }
//    }
//    .throttle(1, 50 milliseconds, 1, ThrottleMode.shaping)
//    .via(pool)
//    .runForeach {
//      case (scala.util.Success(response), data) =>
//        //println(s"Result for: $data was successful: $response")
//        response.discardEntityBytes() // don't forget this
//      case (Failure(ex), data) =>
//        println(s"$data failed with $ex")
//    }
//}
object LoadTest extends App {
  implicit val system = ActorSystem()
  implicit def ec = system.dispatcher
  implicit val materializer = ActorMaterializer()


  var cont = 0
  val connection = Connection(ConfigFactory.load())

  val pub = new RabbitPublisher(connection)

  Source.repeat(1)
    .map{ _ =>
      val data = DeviceSensorsUpdated(UUID.randomUUID().toString,"DeviceSensorsUpdated",LocalDateTime.now(), cont.toString, 44.1391, 12.24315 , Map("source" -> (cont == 0).toString))
      cont = (cont + 1) % 1000
      data
    }
    //.throttle(1, 500 microseconds, 1, ThrottleMode.shaping)
    .map(m => {println(m.deviceId);m})
    .runWith(pub.sinkToRabbit("sensor_events", "DeviceSensorsUpdated"))

}