package it.unibo.scafi.cobalt.executionService.impl

import it.unibo.scafi.cobalt.common.{ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.core.{CobaltBasicIncarnation, ExecutionGatewayComponent, ExecutionRepositoryComponent, ExecutionServiceComponent}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait CobaltExecutionServiceComponent extends ExecutionServiceComponent{ self : CobaltExecutionServiceComponent.dependencies =>
  override def service = new CobaltService

  class CobaltService() extends ComputingService {
    override def computeNewState(deviceId: String): Future[Either[String, StateImpl]] = {

      val state = for{
        //sensors <- gateway.GetSensors(deviceId)
        nbrs <- gateway.GetAllNbrsIds(deviceId)
        nbrsExports <- repository.mGet(nbrs)
        s = new STATE(deviceId, nbrsExports.length.toString)
        res <- repository.set(deviceId, s)
      }yield s

      state.map(Right(_))
    }
  }
}

object CobaltExecutionServiceComponent {
  type dependencies = ExecutionRepositoryComponent with ExecutionGatewayComponent with CobaltBasicIncarnation with ExecutionContextProvider
}
