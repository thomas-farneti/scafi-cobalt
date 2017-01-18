package it.unibo.scafi.cobalt.core.network

import it.unibo.scafi.cobalt.core.messages.Messages

/**
  * Created by tfarneti on 18/01/2017.
  */
trait NetworkReadModel { self: Messages =>
  case class GetAllDevices(netId: String) extends Query
  case class GetAllDevicesResponse(deviceIds:Set[String])

  trait GetAllDevicesHandler extends QueryHandler[GetAllDevices,GetAllDevicesResponse]{
    override def query(req: GetAllDevices) = new GetAllDevicesResponse(Set.empty)
  }

  class NetworkReadModel extends GetAllDevicesHandler{

  }
}
