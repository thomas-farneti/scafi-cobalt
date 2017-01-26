package it.unibo.scafi.cobalt.computingService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ComputingServiceComponent { self: ComputingServiceCore =>
  def service: ComputingService

  trait ComputingService{
    def computeNewState(deviceId:ID):Future[Either[String,STATE]]
  }
}
