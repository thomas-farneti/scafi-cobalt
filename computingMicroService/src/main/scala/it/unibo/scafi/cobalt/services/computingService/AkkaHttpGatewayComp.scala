package it.unibo.scafi.cobalt.services.computingService

import it.unibo.scafi.cobalt.core.services.computingService.ComputingGatewayComponent
import it.unibo.scafi.incarnations.BasicAbstractIncarnation

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by tfarneti.
  */
trait AkkaHttpGatewayComp extends ComputingGatewayComponent{ self : BasicAbstractIncarnation =>
  override def gateway = new AkkaHttpGateway

  class AkkaHttpGateway extends Gateway{
    override def GetAllNbrsIds(id: Int): Try[Future[Set[Int]]] = ???
  }
}
