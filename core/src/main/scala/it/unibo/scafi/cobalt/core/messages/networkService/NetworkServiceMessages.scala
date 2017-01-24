package it.unibo.scafi.cobalt.core.messages.networkService

/**
  * Created by tfarneti.
  */
object NetworkServiceMessages {
  sealed trait NetworkServiceCommand

  case class GetNeighborsCmd(id:String) extends NetworkServiceCommand
  case class AddNeighborForDeviceCmd(deviceId: String, nbrId: String) extends NetworkServiceCommand
  case class RemoveNeighborForDeviceCmd(deviceId: String, nbrId:String) extends NetworkServiceCommand
}
