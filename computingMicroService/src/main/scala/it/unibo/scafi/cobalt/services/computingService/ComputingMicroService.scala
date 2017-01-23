package it.unibo.scafi.cobalt.services.computingService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.core.messages.computingService.ComputingServiceMessages.ComputeNewState
import it.unibo.scafi.cobalt.core.services.computingService.{BasicCobaltIncarnation, BasicComputingServiceComponent, RedisComputingRepositoryComponent}
import it.unibo.scafi.incarnations.BasicAbstractIncarnation

/**
  * Created by tfarneti.
  */
object ComputingMicroService extends App with Config{
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val env = new BasicComputingServiceComponent with RedisComputingRepositoryComponent with AkkaHttpGatewayComp with BasicCobaltIncarnation


  val routes = (path("test" / Segment) & get){ id=>
    complete{
      env.service.computeNewState(ComputeNewState(id)).map[ToResponseMarshallable] {
        _ => OK
      }
    }
  }

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))

}
