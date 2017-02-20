package it.unibo.scafi.cobalt.executionService.impl.scafi

import it.unibo.scafi.cobalt.executionService.core.ExecutionGatewayComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ScafiMockExecutionGatewayComponent extends ExecutionGatewayComponent{ self: ScafiIncarnation =>
  override def gateway = new ScafiGateway

    class ScafiGateway extends Gateway{
      private val nbrs = Map(
        "1"-> Set[String]("2"),
        "2" -> Set[String]("1","3"),
        "3" -> Set[String]("2")
      )

      private val localSensors = Map(
        "1" -> Map("source" -> true),
        "2" -> Map("source" -> false),
        "3" -> Map("source" -> false)
      )

      override def getAllNbrsIds(id: String): Future[Set[String]] = Future.successful(nbrs(id))

      override def sense(id: String, sensorName: String): Future[Any] = Future.successful(true)

      override def nbrSensorsSense(nbrsIds: Set[String]): Future[Map[String, Map[String, Any]]] = {
        if (nbrsIds.contains("1") && nbrsIds.contains("3")) {
          Future.successful( Map[String, Map[String, Any]]("nbrRange" -> Map[String,Double]("1" -> 10 ,"2" -> 10, "3" -> 10 )))
        } else {
          Future.successful( Map[String, Map[String, Any]]("nbrRange" -> Map[String,Double]("1" -> 10 ,"2" -> 10, "3" -> 10 )))
        }
      }

      override def localSensorsSense(id: String): Future[Map[String, Any]] = Future.successful(localSensors(id))
    }
}
