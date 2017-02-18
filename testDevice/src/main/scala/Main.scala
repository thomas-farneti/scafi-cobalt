import java.nio.file.{Path, Paths}

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.javadsl.model.HttpEntities
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
import it.unibo.scafi.cobalt.common.messages.SensorData

import scala.concurrent.duration._
import scala.util.Failure



object Main extends App{
  implicit val system = ActorSystem()
  implicit def ec =  system.dispatcher
  implicit val materializer = ActorMaterializer()

  private val log = Logging(system, getClass.getName)

  val r = new scala.util.Random

  val pool = Http().cachedHostConnectionPool[SensorData]("localhost",8080)
  //val lines = scala.io.Source.fromFile("/Users/Thomas/IdeaProjects/scafi-cobalt/testDevice/src/main/Resources/points.txt").
  var cont = 0




  FileIO.fromPath(Paths.get("/Users/Thomas/IdeaProjects/scafi-cobalt/testDevice/src/main/Resources/points.txt")).
    via(Framing.delimiter(ByteString.fromString(System.lineSeparator()), 512, allowTruncation = true)).
    map(_.utf8String)
    .mapAsync(1){ l =>
      val cols = l.split("\t")

      val data = SensorData(cont.toString, cont.toString, "gps", s"${cols(1)}:${cols(3)}")
      cont = (cont + 1) % 500

      Marshal(data).to[RequestEntity].map{ e =>
        HttpRequest(method=HttpMethods.POST, uri = Uri("/sensorData"), entity= e) -> data
      }
    }
    .throttle(1,50 milliseconds,1,ThrottleMode.shaping)
    .via(pool)
    .runForeach {
      case ( scala.util.Success(response), data) =>
        println(s"Result for: $data was successful: $response")
        response.discardEntityBytes() // don't forget this
      case (Failure(ex), data) =>
        println(s"$data failed with $ex")
    }

//  Source.single(1)
//    .mapAsync(1){ i =>
//      val lat = "44."+ (10328 + r.nextInt(( 147697 - 10328) + 1))
//      val lon = "12."+(159167 + r.nextInt(( 281476 - 159167) + 1))
//      val data = SensorData(i.toString, i.toString, "gps", s"$lat:$lon")
//
//      Marshal(data).to[RequestEntity].map{ e =>
//        HttpRequest(method=HttpMethods.POST, uri = Uri("/sensorData"), entity= e) -> data
//      }
//    }
//    .throttle(1,1.second,1,ThrottleMode.shaping)
//    .via(pool)
//    .runForeach {
//      case ( scala.util.Success(response), data) =>
//        println(s"Result for: $data was successful: $response")
//        response.discardEntityBytes() // don't forget this
//      case (Failure(ex), data) =>
//        println(s"$data failed with $ex")
//    }

}
