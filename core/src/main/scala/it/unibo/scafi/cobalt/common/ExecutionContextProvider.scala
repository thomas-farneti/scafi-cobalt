package it.unibo.scafi.cobalt.common

import scala.concurrent.ExecutionContext

/**
  * Created by tfarneti.
  */
trait ExecutionContextProvider {
  implicit val executionContex : ExecutionContext
}
