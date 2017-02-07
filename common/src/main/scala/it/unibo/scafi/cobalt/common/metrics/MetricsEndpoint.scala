package it.unibo.scafi.cobalt.common.metrics

import akka.http.scaladsl.server.Directives._
import io.prometheus.client.CollectorRegistry

class MetricsEndpoint(registry: CollectorRegistry) {

  val metricsRoute = {
    get {
      path("metrics") {
        complete {
          MetricFamilySamplesEntity.fromRegistry(registry)
        }
      }
    }
  }

}
