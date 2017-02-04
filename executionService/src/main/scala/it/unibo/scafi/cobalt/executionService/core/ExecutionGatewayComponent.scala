package it.unibo.scafi.cobalt.executionService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ExecutionGatewayComponent {
  def gateway : Gateway

  trait Gateway{
    def getAllNbrsIds(id: String): Future[Set[String]]
    def senseAll(id:String) : Future[Map[String,String]]
    def sense(id:String, sensorName:String): Future[String]
  }
}

trait ExecutionGatewayMockComponent extends ExecutionGatewayComponent{
  def gateway = new MockGateway

  class MockGateway extends Gateway{
    override def getAllNbrsIds(id: String): Future[Set[String]] = Future.successful(Set("2","3"))

    override def senseAll(id: String): Future[Map[String, String]] = Future.successful(Map("source"->"true"))

    override def sense(id: String, sensorName: String): Future[String] = Future.successful("10")
  }
}