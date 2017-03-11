package it.unibo.scafi.cobalt.executionService.core

import scala.concurrent.Future

import ScafiCobaltIncarnation._

/**
  * Created by tfarneti.
  */
trait ExecutionServiceComponent {
  def service: ExecutionService

  trait ExecutionService{
    def execRound(deviceId:ID):Future[EXPORT]
    def fetchState(deviceId:ID): Future[EXPORT]
  }
}
