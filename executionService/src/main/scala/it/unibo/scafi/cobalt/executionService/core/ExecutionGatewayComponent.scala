package it.unibo.scafi.cobalt.executionService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ExecutionGatewayComponent {
  def gateway : Gateway

  trait Gateway{
    def GetAllNbrsIds(id: String): Future[Set[String]]
    def GetSensors(id:String) : Future[Map[String,String]]
  }
}

trait ExecutionGatewayMockComponent extends ExecutionGatewayComponent{
  def gateway = new MockGateway

  class MockGateway extends Gateway{
    override def GetAllNbrsIds(id: String): Future[Set[String]] = Future.successful(Set("2","3"))

    override def GetSensors(id: String): Future[Map[String, String]] = Future.successful(Map("source"->"true"))
  }
}