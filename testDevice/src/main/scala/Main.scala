import java.io.IOException

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.StatusCodes.Success
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import it.unibo.scafi.cobalt.core.messages.JsonProtocol._
import it.unibo.scafi.cobalt.core.messages.SensorData

import scala.concurrent.Future
import scala.util.Random


object Main extends App{
  implicit val system = ActorSystem()
  implicit def ec =  system.dispatcher
  implicit val materializer = ActorMaterializer()

  private val log = Logging(system, getClass.getName)

  val r = new scala.util.Random

  1 to 500 foreach{ i =>

    val lat = "44."+ (10328 + r.nextInt(( 147697 - 10328) + 1))
    val lon = "12."+(159167 + r.nextInt(( 281476 - 159167) + 1))

    Marshal(SensorData(i.toString, i.toString, "gps", s"$lat:$lon")).to[RequestEntity].flatMap{ e =>
      Http().singleRequest(HttpRequest(method=HttpMethods.POST, uri = Uri("http://localhost:80/sensorData"), entity= e))
    }.map { x =>
      x.status match {
        case status:StatusCode if status.isSuccess() => { log.info(Unmarshal(x.entity).to[String] +" "+i) }
        case status:StatusCode if status.isFailure() => { log.info("Failed") }
      }
    }
    //LatLon(44.147697,12.159167),LatLon(44.10328,12.281476)
    Thread.sleep(250)
  }
}
