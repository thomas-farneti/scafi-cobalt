package it.unibo.scafi.cobalt.core.services.computingService

import it.unibo.scafi.incarnations.{BasicAbstractIncarnation, Incarnation}

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by tfarneti.
  */
trait ComputingGatewayComponent { self: BasicAbstractIncarnation =>
  def gateway : Gateway

  trait Gateway{
    def GetAllNbrsIds(id: ID): Try[Future[Set[ID]]]
  }
}

trait ComputingMockGateway extends ComputingGatewayComponent{ self : BasicAbstractIncarnation =>
  def gateway = new MockGateway

  class MockGateway extends Gateway{
    override def GetAllNbrsIds(id: ID): Try[Future[Set[ID]]] = Try(Future.successful(Set.empty[ID]))
  }
}