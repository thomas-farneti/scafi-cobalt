package it.unibo.scafi.cobalt.computingService.impl

import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingServiceGatewayComponent}
import it.unibo.scafi.cobalt.core.incarnation.ScafiCobaltIncarnation

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by tfarneti.
  */
trait AkkaHttpServiceGatewayComp extends ComputingServiceGatewayComponent{ self : CobaltBasicIncarnation =>
  override def gateway = new AkkaHttpGateway

  class AkkaHttpGateway extends Gateway{

    override def GetSensors(id: String): Future[Map[String, String]] = ???

    override def GetAllNbrsIds(id: String): Future[Set[String]] = ???
  }
}
