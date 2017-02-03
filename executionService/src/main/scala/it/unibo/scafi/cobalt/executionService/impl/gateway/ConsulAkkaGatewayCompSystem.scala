package it.unibo.scafi.cobalt.executionService.impl.gateway

import java.io.IOException
import java.net.InetAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Sink, Source}
import consul.Consul
import it.unibo.scafi.cobalt.common.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.core.{CobaltBasicIncarnation, ExecutionGatewayComponent}
import it.unibo.scafi.cobalt.executionService.impl.ConsulConfiguration
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ConsulAkkaGatewayCompSystem extends ExecutionGatewayComponent { self : CobaltBasicIncarnation
  with ConsulConfiguration
  with ExecutionContextProvider
  with ActorSystemProvider
  with ActorMaterializerProvider =>

  override def gateway = new ConsulAkkaHttpGateway()

  class ConsulAkkaHttpGateway extends Gateway{
    val consul: Consul = Consul.standalone(InetAddress.getByName(consulHost),consulPort)

    override def GetSensors(id: String): Future[Map[String, String]] = {
      import consul.v1._

      val endpoints = consul.v1.catalog.service(ServiceType("sensorsService")).map(s => s.map(n=> (n.Address,n.ServicePort)))
      val request = RequestBuilding.Get(s"/device/$id/sensor/gps")

      endpoints.map(s => Source.single(request).via(Http().outgoingConnection(s.head._1, s.head._2)).runWith(Sink.head)).flatMap(r =>r.flatMap {response =>
        response.status match {
          case Success(_) => Unmarshal(response.entity).to[Map[String,String]]
          case _ => Future.failed(new IOException("Epic Fail"))
        }
      })
    }

    override def GetAllNbrsIds(id: String): Future[Set[String]] = {
      import consul.v1._

      val endpoints = consul.v1.catalog.service(ServiceType("networkService")).map(s => s.map(n=> (n.Address,n.ServicePort)))
      val request = RequestBuilding.Get(s"nbrs/spatial/$id")

      endpoints.map(s => Source.single(request).via(Http().outgoingConnection(s.head._1, s.head._2)).runWith(Sink.head)).flatMap(r =>r.flatMap {response =>
        response.status match {
          case Success(_) => Unmarshal(response.entity).to[Set[String]]
          case _ => Future.failed(new IOException("Epic Fail"))
        }
      })
    }
  }
}
