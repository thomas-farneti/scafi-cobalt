package it.unibo.scafi.cobalt.computingService.impl

import java.lang.management.ManagementFactory

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingServiceComponent}

import scala.concurrent.duration.{Duration, MILLISECONDS}
/**
  * Created by tfarneti.
  */


trait AkkaComputingServiceComponent { self: AkkaComputingServiceComponent.dependencies =>
  val computingServiceRoutes = {
    path("compute" / Segment){ deviceId =>
      post {
        complete {
          service.computeNewState(deviceId).map[ToResponseMarshallable] {
            case Right(s) => deviceId+" -> "+s.export
            case Left(m) => InternalServerError -> m
          }
        }
      }
    }~ healthRoutes
  }

  def healthRoutes = pathPrefix("health") {
    path("ping") {
      get {
        complete("OK")
      }
    } ~ path("uptime") {
      get {
        complete(getUptime.toString)
      }
    }
  }

  private def getUptime = Duration(ManagementFactory.getRuntimeMXBean.getUptime, MILLISECONDS).toSeconds
}


object AkkaComputingServiceComponent{
  type dependencies = ComputingServiceComponent with CobaltBasicIncarnation with ActorSystemProvider
}
