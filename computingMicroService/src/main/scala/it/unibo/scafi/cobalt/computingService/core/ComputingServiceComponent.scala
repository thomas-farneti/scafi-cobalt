package it.unibo.scafi.cobalt.computingService.core

import it.unibo.scafi.cobalt.core.incarnation.BasicCobaltIncarnation
import it.unibo.scafi.cobalt.core.messages.computingService.ComputeNewStateCommand

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ComputingServiceComponent { self: ComputingServiceComponent.dependencies =>
  def service = new BasicComputingService

  class BasicComputingService {
    def get(id: ID): Future[STATE] = ???

    def computeNewState(cmd: ComputeNewStateCommand): Future[Either[String,STATE]] = {
      for {
      //        nbrs <- gateway.GetAllNbrsIds(cmd.id).map(_ _)
        state <- Future.successful(new STATE(cmd.id, new EXPORT()))
        s <-  repository.set(cmd.id, state)

      } yield Right(state)
    }
  }
}

object ComputingServiceComponent {
  type dependencies = BasicAbstractComputingRepositoryComponent  with ComputingGatewayComponent with BasicCobaltIncarnation
}