package it.unibo.scafi.cobalt.computingService.impl

import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingServiceGatewayComponent}
import it.unibo.scafi.cobalt.core.incarnation.ScafiCobaltIncarnation

import scala.concurrent.ExecutionContext.Implicits.global
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
  type dependencies =  ComputingServiceGatewayComponent with ScafiCobaltIncarnation with CobaltBasicIncarnation
}