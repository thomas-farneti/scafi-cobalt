import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import it.unibo.scafi.cobalt.core.messages.JsonProtocol._
import it.unibo.scafi.cobalt.core.messages.SensorData
import spray.json._


object Main extends App{
  implicit val system = ActorSystem()
  implicit def ec =  system.dispatcher // to get an implicit ExecutionContext into scope
  implicit val materializer = ActorMaterializer()


  Source(1 to 1000)
    .map(i => Marshal(SensorData(i.toString, i.toString, "gps", "44.13965026409682:12.246460430324078").toJson.compactPrint).to[RequestEntity] -> i)
    .runForeach(e  =>{
      val body = e._1.value.get.get.withContentType(ContentTypes.`application/json`)
      Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = "http://localhost:80/sensorData", entity = body)).map(_.discardEntityBytes())
      println(e._2)
    })
}
