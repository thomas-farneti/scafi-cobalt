package it.unibo.scafi.cobalt.computingService.impl

import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingRepositoryComponent, ComputingServiceComponent, ComputingServiceGatewayComponent}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by tfarneti.
  */
trait CobaltComputingServiceComponent extends ComputingServiceComponent{ self : ComputingRepositoryComponent with ComputingServiceGatewayComponent with CobaltBasicIncarnation =>
  override def service = new CobaltService

  class CobaltService() extends ComputingService {
    override def computeNewState(deviceId: String): Future[Either[String, StateImpl]] = {
      var sensors = gateway.GetSensors(deviceId)

      val exports = for{
        nbrs <- gateway.GetAllNbrsIds(deviceId)
        nbrsExports <- repository.mGet(nbrs)
      }yield nbrsExports

      val state = new STATE(deviceId,exports.map(_.length).value.toString)

      repository.set(deviceId, state).map{
        case true => Right(state)
        case _ => Left("Errore")
      }
    }
  }

}
