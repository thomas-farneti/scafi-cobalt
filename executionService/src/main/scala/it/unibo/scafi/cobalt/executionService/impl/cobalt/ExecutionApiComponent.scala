package it.unibo.scafi.cobalt.executionService.impl.cobalt

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.hotspot.DefaultExports
import it.unibo.scafi.cobalt.common.infrastructure.{ActorMaterializerProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.common.metrics.MetricsEndpoint
import it.unibo.scafi.cobalt.executionService.core.ExecutionServiceComponent
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
          val state = service.execRound(deviceId)
          complete {
            state.map[ToResponseMarshallable] { s =>
              s.toString()
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
