package it.unibo.scafi.cobalt.computingService.core

import it.unibo.scafi.cobalt.core.incarnation.ScafiCobaltIncarnation
import it.unibo.scafi.incarnations.Incarnation

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by tfarneti.
  */
trait ComputingServiceGatewayComponent {
  def gateway : Gateway

  trait Gateway{
    def GetAllNbrsIds(id: String): Future[Set[String]]
    def GetSensors(id:String) : Future[Map[String,String]]
  }
}

trait ComputingMockServiceGateway extends ComputingServiceGatewayComponent{
  def gateway = new MockGateway

  class MockGateway extends Gateway{
    override def GetAllNbrsIds(id: String): Future[Set[String]] = Future.successful(Set("2","3"))

    override def GetSensors(id: String): Future[Map[String, String]] = Future.successful(Map("source"->"true"))
  }
}