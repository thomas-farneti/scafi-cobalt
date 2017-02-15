package it.unibo.scafi.cobalt.executionService.impl.cobalt

import java.io.IOException

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes.Success
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.common.infrastructure.{ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.core.ExecutionGatewayComponent
import it.unibo.scafi.cobalt.executionService.impl.ServicesConfiguration
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait CobaltExecutionGatewayComponent extends ExecutionGatewayComponent{ self: CobaltBasicIncarnation with ServicesConfiguration with ActorSystemProvider with ExecutionContextProvider =>
  override def gateway = new CobaltExecutionGateway()

  class CobaltExecutionGateway extends Gateway{
    implicit val materilizer = ActorMaterializer()

    override def getAllNbrsIds(id: String): Future[Set[String]] = {
      Http().singleRequest(HttpRequest(uri = s"http://$domainHost:$domainPort/nbrs/spatial/$id")).flatMap { response =>
        response.status match {
          case Success(_) => Unmarshal(response.entity).to[Set[String]]
          case _ => Future.failed(new IOException("Epic Fail"))
        }
      }
    }

    override def sense(id: String, sensorName: String): Future[String] = {
      Http().singleRequest(HttpRequest(uri= s"http://$sensorHost:$sensorPort/device/$id/sensor/$sensorName")).flatMap { response =>
        response.status match {
          case Success(_) => Unmarshal(response.entity).to[String]
          case _ => Future.failed(new IOException("Epic Fail"))
        }
      }
    }

    override def nbrSensorsSense(nbrsIds: Set[String]): Future[Map[String, Map[String, Any]]] = ???

    override def localSensorsSense(id: String): Future[Map[String, Any]] = ???
  }
}
