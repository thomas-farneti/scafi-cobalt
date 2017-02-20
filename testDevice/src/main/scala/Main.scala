import java.nio.file.{Path, Paths}
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
import it.unibo.scafi.cobalt.common.messages.JsonProtocol._
import it.unibo.scafi.cobalt.common.messages.DeviceData

import scala.concurrent.duration._
import scala.util.Failure


object Main extends App {
  implicit val system = ActorSystem()

  implicit def ec = system.dispatcher

  implicit val materializer = ActorMaterializer()

  private val log = Logging(system, getClass.getName)

  val r = new scala.util.Random

  val pool = Http().cachedHostConnectionPool[DeviceData]("localhost", 8080)
  //val lines = scala.io.Source.fromFile("/Users/Thomas/IdeaProjects/scafi-cobalt/testDevice/src/main/Resources/points.txt").getLines().toArray
  var cont = 0


  //    FileIO.fromPath(Paths.get("/Users/Thomas/IdeaProjects/scafi-cobalt/testDevice/src/main/Resources/points.txt")).
  //    via(Framing.delimiter(ByteString.fromString(System.lineSeparator()), 512, allowTruncation = true)).
  //    map(_.utf8String)
  Source.cycle(() => scala.io.Source.fromFile("/Users/Thomas/IdeaProjects/scafi-cobalt/testDevice/src/main/Resources/points.txt").getLines())
    .mapAsync(1) { l =>
      val cols = l.split("\t")

      val data = DeviceData(UUID.randomUUID().toString, cont.toString, cols(1).toDouble, cols(3).toDouble, Map("source" -> (cont == 0).toString))
      cont = (cont + 1) % 1000

      Marshal(data).to[RequestEntity].map { e =>
        HttpRequest(method = HttpMethods.POST, uri = Uri("/deviceDataStream"), entity = e) -> data
      }
    }
    .throttle(1, 15 milliseconds, 1, ThrottleMode.shaping)
    .via(pool)
    .runForeach {
      case (scala.util.Success(response), data) =>
        //println(s"Result for: $data was successful: $response")
        response.discardEntityBytes() // don't forget this
      case (Failure(ex), data) =>
        println(s"$data failed with $ex")
    }
}
