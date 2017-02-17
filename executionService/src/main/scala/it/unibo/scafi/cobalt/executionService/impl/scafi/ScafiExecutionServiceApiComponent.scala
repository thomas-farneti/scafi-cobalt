package it.unibo.scafi.cobalt.executionService.impl.scafi

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives.{complete, extractRequestEntity, path, post, _}
import akka.stream.ActorMaterializer
import io.prometheus.client.CollectorRegistry
import it.unibo.scafi.cobalt.common.metrics.MetricsEndpoint

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */
class ScafiExecutionServiceApiComponent(serviceCmp : ScafiExecutionServiceComponent)(implicit val mat: ActorMaterializer, implicit val executionContextExecutor: ExecutionContextExecutor) {
  val metricsEndpoint = new MetricsEndpoint(CollectorRegistry.defaultRegistry)

  val executionRoutes = {
    path("compute" / Segment){ deviceId =>
      post {
        extractRequestEntity{ entity =>
          entity.discardBytes()
          val newState = serviceCmp.service.execRound(deviceId)

          complete {
            newState.map[ToResponseMarshallable] { s =>
              s.toString()
            }
          }
        }
      }
    }~ metricsEndpoint.metricsRoute
  }
}
