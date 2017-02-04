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


object Main extends App{
  implicit val system = ActorSystem()
  implicit def ec =  system.dispatcher
  implicit val materializer = ActorMaterializer()

  private val log = Logging(system, getClass.getName)

  1001 to 2000 foreach{ i =>

    Marshal(SensorData(i.toString, i.toString, "gps", "44.13965026409682:12.246460430324078")).to[RequestEntity].flatMap{ e =>
      Http().singleRequest(HttpRequest(method=HttpMethods.POST, uri = Uri("http://localhost:80/sensorData"), entity= e))
    }.map { x =>
      x.status match {
        case status:StatusCode if status.isSuccess() => { log.info(Unmarshal(x.entity).to[String] +" "+i) }
        case status:StatusCode if status.isFailure() => { log.info("Failed") }
      }
    }

    Thread.sleep(15)
  }
}
