package it.unibo.scafi.cobalt.executionService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ExecutionServiceComponent { self: ExecutionServiceCore =>
  def service: ComputingService

  trait ComputingService{
    def computeNewState(deviceId:ID):Future[STATE]
  }
}
