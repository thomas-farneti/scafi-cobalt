package it.unibo.scafi.cobalt.executionService.impl.scafi

import it.unibo.scafi.cobalt.common.infrastructure.{ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.core.ExecutionGatewayComponent
import it.unibo.scafi.cobalt.executionService.impl.ServicesConfiguration

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ScafiExecutionGatewayComponent extends ExecutionGatewayComponent{ self: ScafiExecutionGatewayComponent.dependencies =>
  override def gateway = new ScafiGateway

  class ScafiGateway extends Gateway{
    override def getAllNbrsIds(id: String): Future[Set[String]] = ???

    override def sense(id: String, sensorName: String): Future[Any] = ???

    override def nbrSensorsSense(nbrsIds: Set[String]): Future[Map[String, Map[String, Any]]] = ???

    override def localSensorsSense(id: String): Future[Map[String, Any]] = ???
  }
}

object ScafiExecutionGatewayComponent{
  type dependencies =  ServicesConfiguration with ActorSystemProvider with ExecutionContextProvider
}