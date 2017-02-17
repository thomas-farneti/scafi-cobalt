package it.unibo.scafi.cobalt.executionService.impl.cobalt

import it.unibo.scafi.cobalt.executionService.core.ExecutionGatewayComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ExecutionGatewayMockComponent extends ExecutionGatewayComponent{ self: CobaltBasicIncarnation =>
  def gateway = new MockGateway

  class MockGateway extends Gateway{
    override def getAllNbrsIds(id: String): Future[Set[String]] = {
      id match {
        case "1" => Future.successful(Set("2"))
        case "2" => Future.successful(Set("1","3"))
        case "3" => Future.successful(Set("2"))
      }
    }

    override def sense(id: String, sensorName: String): Future[String] = Future.successful("10")

    override def nbrSensorsSense(nbrsIds: Set[String]): Future[Map[String, Map[String, Any]]] = Future.successful(Map())

    override def localSensorsSense(id: String): Future[Map[String, Any]] = {
      id match {
        case "1" => Future.successful(Map("source" -> true))
        case "2" => Future.successful(Map("source" -> false))
        case "3" => Future.successful(Map("source" -> false))
      }
    }
  }
}
