package it.unibo.scafi.cobalt.executionService.impl.cobalt

import it.unibo.scafi.cobalt.executionService.core.ExecutionGatewayComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ExecutionGatewayMockComponent extends ExecutionGatewayComponent{ self: CobaltBasicIncarnation =>
  def gateway = new MockGateway

  class MockGateway extends Gateway{
    override def getAllNbrsIds(id: String): Future[Set[String]] = Future.successful(Set("2","3"))

    override def sense(id: String, sensorName: String): Future[String] = Future.successful("10")

    override def nbrSensorsSense(nbrsIds: Set[String]): Future[Map[String, Map[String, Any]]] = Future.successful(Map("source" -> Map("2"->10 , "3" -> 10)))

    override def localSensorsSense(id: String): Future[Map[String, Any]] = Future.successful(Map("source"->"10"))
  }
}
