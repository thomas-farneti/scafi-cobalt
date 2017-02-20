package it.unibo.scafi.cobalt.common.infrastructure

import scala.concurrent.ExecutionContext

/**
  * Created by tfarneti.
  */
trait ExecutionContextProvider {
  implicit def impExecutionContext : ExecutionContext
}
