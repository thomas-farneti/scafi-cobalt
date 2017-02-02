package it.unibo.scafi.cobalt.executionService.impl

import it.unibo.scafi.cobalt.executionService.core.{CobaltBasicIncarnation, ExecutionRepositoryComponent, ExecutionServiceComponent, ExecutionGatewayComponent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait CobaltExecutionServiceComponent extends ExecutionServiceComponent{ self : ExecutionRepositoryComponent with ExecutionGatewayComponent with CobaltBasicIncarnation =>
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
