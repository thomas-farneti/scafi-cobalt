package it.unibo.scafi.cobalt.executionService.core

import scala.concurrent.Future
import ScafiCobaltIncarnation._
/**
  * Created by tfarneti.
  */
trait ExecutionGatewayComponent {
  def gateway : Gateway

  trait Gateway{
    def getAllNbrsIds(id: ID): Future[Set[ID]]
    def sense(id:ID, sensorName: LSNS): Future[Any]
    def localSensorsSense(id:ID) : Future[Map[LSNS,Any]]
    def nbrSensorsSense(nbrsIds: Set[ID]): Future[Map[NSNS,Map[ID,Any]]]
  }
}

