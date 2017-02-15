package it.unibo.scafi.cobalt.executionService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ExecutionServiceComponent { self: ExecutionServiceCore =>
  def service: ExecutionService

  trait ExecutionService{
    def execRound(deviceId:ID):Future[STATE]
    def fetchState(deviceId:ID): Future[STATE]
  }
}
