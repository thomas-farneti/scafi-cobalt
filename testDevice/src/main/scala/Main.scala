import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, ThrottleMode}
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

//  1 to 500 foreach{ i =>
//
//    val lat = "44."+ (10328 + r.nextInt(( 147697 - 10328) + 1))
//    val lon = "12."+(159167 + r.nextInt(( 281476 - 159167) + 1))
//
//    Marshal(SensorData(i.toString, i.toString, "gps", s"$lat:$lon")).to[RequestEntity].flatMap{ e =>
//      Http().singleRequest(HttpRequest(method=HttpMethods.POST, uri = Uri("http://localhost:80/sensorData"), entity= e))
//    }.map { x =>
//      x.status match {
//        case status:StatusCode if status.isSuccess() => { log.info(Unmarshal(x.entity).to[String] +" "+i) }
//        case status:StatusCode if status.isFailure() => { log.info("Failed") }
//      }
//    }
//    //LatLon(44.147697,12.159167),LatLon(44.10328,12.281476)
//    Thread.sleep(250)
//  }

  val pool = Http().cachedHostConnectionPool[SensorData]("localhost",8080)

  Source.repeat(1)
    .mapAsync(1){ i =>
      val lat = "44."+ (10328 + r.nextInt(( 147697 - 10328) + 1))
      val lon = "12."+(159167 + r.nextInt(( 281476 - 159167) + 1))
      val data = SensorData(i.toString, i.toString, "gps", s"$lat:$lon")

      Marshal(data).to[RequestEntity].map{ e =>
        HttpRequest(method=HttpMethods.POST, uri = Uri("/sensorData"), entity= e) -> data
      }
    }
    .throttle(1,1.second,1,ThrottleMode.shaping)
    .via(pool)
    .runForeach {
      case ( scala.util.Success(response), data) =>
        println(s"Result for: $data was successful: $response")
        response.discardEntityBytes() // don't forget this
      case (Failure(ex), data) =>
        println(s"$data failed with $ex")
    }

}
