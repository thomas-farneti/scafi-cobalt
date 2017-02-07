package it.unibo.scafi.cobalt.common

import scala.concurrent.ExecutionContext

/**
  * Created by tfarneti.
  */
trait ExecutionContextProvider {
  implicit def impExecutionContext : ExecutionContext
}
