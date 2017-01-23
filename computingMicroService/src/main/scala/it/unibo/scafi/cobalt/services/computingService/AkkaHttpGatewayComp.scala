package it.unibo.scafi.cobalt.services.computingService

import it.unibo.scafi.cobalt.core.services.computingService.{BasicCobaltIncarnation, ComputingGatewayComponent}
import it.unibo.scafi.incarnations.BasicAbstractIncarnation

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
