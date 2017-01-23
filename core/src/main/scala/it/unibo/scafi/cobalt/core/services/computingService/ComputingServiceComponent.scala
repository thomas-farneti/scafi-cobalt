package it.unibo.scafi.cobalt.core.services.computingService

import it.unibo.scafi.cobalt.core.messages.computingService.ComputingServiceMessages.ComputeNewState
import it.unibo.scafi.incarnations.BasicAbstractIncarnation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ComputingServiceComponent { self: ComputingRepositoryComponent with BasicCobaltIncarnation =>
  def service: ComputingService

  trait ComputingService {
    def get(id: ID): Future[STATE]

    def computeNewState(cmd: ComputeNewState): Future[STATE]
  }
}

trait BasicComputingServiceComponent extends ComputingServiceComponent{ self: BasicAbstractComputingRepositoryComponent
  with ComputingGatewayComponent
  with BasicCobaltIncarnation =>

  override def service = new BasicComputingService

  class BasicComputingService extends ComputingService{
    override def get(id: ID): Future[STATE] = ???

    override def computeNewState(cmd: ComputeNewState): Future[STATE] = {
      for{
//        nbrs <- gateway.GetAllNbrsIds(cmd.id).map(_ _)
        state <- Future.successful(new STATE(cmd.id,new EXPORT()))
        s <-repository.set(cmd.id, state)

      }yield state
    }
  }
}