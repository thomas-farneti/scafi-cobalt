package it.unibo.scafi.cobalt.computingService.impl

import it.unibo.scafi.cobalt.computingService.core.ComputingGatewayComponent
import it.unibo.scafi.cobalt.core.incarnation.BasicCobaltIncarnation

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by tfarneti.
  */
trait AkkaHttpGatewayComp extends ComputingGatewayComponent{ self : BasicCobaltIncarnation =>
  override def gateway = new AkkaHttpGateway

  class AkkaHttpGateway extends Gateway{
    override def GetAllNbrsIds(id: String): Try[Future[Set[String]]] = ???
  }
}
