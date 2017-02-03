package it.unibo.scafi.cobalt.executionService.impl.gateway

import java.io.IOException

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes.Success
import akka.http.scaladsl.unmarshalling.Unmarshal
import it.unibo.scafi.cobalt.common.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.core.ExecutionGatewayComponent
import it.unibo.scafi.cobalt.executionService.impl.ServicesConfiguration
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait DockerGatewayComponent extends ExecutionGatewayComponent{ self: ServicesConfiguration with ActorSystemProvider with ActorMaterializerProvider with ExecutionContextProvider =>
  override def gateway = new DockerGateway()

  class DockerGateway extends Gateway{

    override def GetAllNbrsIds(id: String): Future[Set[String]] = {
      Http().singleRequest(HttpRequest(uri = s"http://$domainHost:$domainPort/nbrs/spatial/$id")).flatMap { response =>
        response.status match {
          case Success(_) => Unmarshal(response.entity).to[Set[String]]
          case _ => Future.failed(new IOException("Epic Fail"))
        }
      }
    }

    override def GetSensors(id: String): Future[Map[String, String]] = {
      Future.successful(Map())
    }

    def sense(id: String, lsname:String): Future[String] = {
      Http().singleRequest(HttpRequest(uri= s"http://$sensorHost:$sensorPort/device/$id/sensor/$lsname")).flatMap { response =>
        response.status match {
          case Success(_) => Unmarshal(response.entity).to[String]
          case _ => Future.failed(new IOException("Epic Fail"))
        }
      }
    }
  }
}