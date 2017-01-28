package it.unibo.scafi.cobalt.computingService.impl

import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingRepositoryComponent, ComputingServiceComponent, ComputingServiceGatewayComponent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait CobaltComputingServiceComponent extends ComputingServiceComponent{ self : ComputingRepositoryComponent with ComputingServiceGatewayComponent with CobaltBasicIncarnation =>
  override def service = new CobaltService

  class CobaltService() extends ComputingService {
    override def computeNewState(deviceId: String): Future[Either[String, StateImpl]] = {
      var sensors = gateway.GetSensors(deviceId)

      val state = for{
        nbrs <- gateway.GetAllNbrsIds(deviceId)
        nbrsExports <- repository.mGet(nbrs)
        s = new STATE(deviceId, nbrsExports.length.toString)
        res <- repository.set(deviceId, s)
      }yield s

      state.map(Right(_))
    }
  }

}
