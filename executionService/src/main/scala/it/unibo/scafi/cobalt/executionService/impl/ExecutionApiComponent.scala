package it.unibo.scafi.cobalt.executionService.impl

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.hotspot.DefaultExports
import it.unibo.scafi.cobalt.common.metrics.MetricsEndpoint
import it.unibo.scafi.cobalt.common.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.core.{CobaltBasicIncarnation, ExecutionServiceComponent}
/**
  * Created by tfarneti.
  */


trait ExecutionApiComponent { self: ExecutionApiComponent.dependencies =>
  val metricsEndpoint = new MetricsEndpoint(CollectorRegistry.defaultRegistry)
  DefaultExports.initialize()

  val executionRoutes = {
    path("compute" / Segment){ deviceId =>
      post {
        extractRequestEntity{ entity =>
          entity.discardBytes()
          complete {
            service.computeNewState(deviceId).map[ToResponseMarshallable] { s =>
              s.id+" -> "+s.export
            }
          }
        }
      }
    }~ metricsEndpoint.metricsRoute
  }
}


object ExecutionApiComponent{
  type dependencies = ExecutionServiceComponent with CobaltBasicIncarnation with ActorMaterializerProvider with ExecutionContextProvider
}
