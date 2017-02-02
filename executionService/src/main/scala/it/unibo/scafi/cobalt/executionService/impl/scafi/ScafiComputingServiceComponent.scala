package it.unibo.scafi.cobalt.executionService.impl.scafi

import it.unibo.scafi.cobalt.core.incarnation.ScafiCobaltIncarnation
import it.unibo.scafi.cobalt.executionService.core.{CobaltBasicIncarnation, ExecutionGatewayComponent}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ScafiComputingServiceComponent { self: ScafiComputingServiceComponent.dependencies =>
  def service = new BasicComputingService

  class BasicComputingService {

    def computeNewState(deviceId:String): Future[Either[String,STATE]] = {
//      var export = factory.emptyExport()
//      export.put(factory.emptyPath(),"")
//
//      for {
//      //        nbrs <- gateway.GetAllNbrsIds(cmd.id).map(_ _)
//        state <- Future.successful(new StateImpl(deviceId,export))
//        s <-  repository.set(deviceId, state)
//
//      } yield Right(state)

      Future.successful(Right(new StateImpl(deviceId,"")))
    }
  }
}

object ScafiComputingServiceComponent {
  type dependencies =  ExecutionGatewayComponent with ScafiCobaltIncarnation with CobaltBasicIncarnation
}