package it.unibo.scafi.cobalt.executionService.impl.scafi

import it.unibo.scafi.cobalt.executionService.core.ExecutionGatewayComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ScafiMockExecutionGatewayComponent extends ExecutionGatewayComponent{ self: ScafiIncarnation =>
  override def gateway = new ScafiGateway

    class ScafiGateway extends Gateway{
      override def getAllNbrsIds(id: String): Future[Set[String]] = Future.successful(Set("2","3"))

      override def sense(id: String, sensorName: String): Future[Any] = Future.successful(true)

      override def nbrSensorsSense(nbrsIds: Set[String]): Future[Map[String, Map[String, Any]]] = Future.successful(
        Map(
          "source" -> Map("2"->false , "3" -> false),
          "nbrRange" -> Map[String,Double]("1" -> 0)
        )
      )

      override def localSensorsSense(id: String): Future[Map[String, Any]] = Future.successful(Map("source"-> true))
    }
}
