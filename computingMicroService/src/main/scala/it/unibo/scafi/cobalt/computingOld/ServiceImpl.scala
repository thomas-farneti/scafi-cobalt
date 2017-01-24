package it.unibo.scafi.cobalt.computingOld

import java.io.IOException

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import it.unibo.scafi.cobalt.computingService.impl.Config
import redis.{ByteStringFormatter, RedisClient}
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by tfarneti.
  */

case class Export(deviceId:String,value:Int)

object Export {
  implicit val byteStringFormatter = new ByteStringFormatter[Export] {
    def serialize(data: Export): ByteString = {
      ByteString(data.deviceId + "|" + data.value)
    }

    def deserialize(bs: ByteString): Export = {
      val r = bs.utf8String.split('|').toList
      Export(r(0), r(1).toInt)
    }
  }
}

class RedisRepositoryImpl (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, ec: ExecutionContext) extends Repository with Config{
  private val redis = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))

  override def loadExports(deviceIds: Set[String]): Future[List[Export]] = {
    redis.mget[Export](deviceIds.toSeq: _*).map(_.toList.map(_.get))
  }
}

class GatewayImplGateway(implicit actorSystem: ActorSystem, materializer: ActorMaterializer, ec: ExecutionContext) extends Gateway with DefaultJsonProtocol with Config{
  private val networkServiceConnectionFlow = Http().outgoingConnection("", 0)

  private def requestNetworkService(request: HttpRequest): Future[HttpResponse] = {
    Source.single(request).via(networkServiceConnectionFlow).runWith(Sink.head)
  }

  override def getNbrs(deviceId: String): Future[Set[String]] = {
    requestNetworkService(RequestBuilding.Get(s"/neighbors/$deviceId")).flatMap {response =>
      response.status match {
        case Success(_) => Unmarshal(response.entity).to[Set[String]]
        case _ => Future.failed(new IOException("Epic Fail"))
      }
    }
  }
}

class Router(service: Service)(implicit actorSystem: ActorSystem, ec: ExecutionContext){
  val routes = (path("test" / Segment) & get){ id=>
    complete{
      service.computeState(new ComputeNewSt(id)).map[ToResponseMarshallable] {
        _ => OK
      }
    }
  }
}

