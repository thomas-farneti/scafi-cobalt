package it.unibo.scafi.cobalt.executionService.impl.scafi

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives.{complete, extractRequestEntity, path, post, _}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.hotspot.DefaultExports
import it.unibo.scafi.cobalt.common.infrastructure.{ActorMaterializerProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.common.metrics.MetricsEndpoint

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

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
              s.id+" -> "+s.export
            }
          }
        }
      }
    }~ metricsEndpoint.metricsRoute
  }
}
