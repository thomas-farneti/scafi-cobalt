package it.unibo.scafi.cobalt.core.services.networkService

import it.unibo.scafi.cobalt.core.incarnation.BasicCobaltIncarnation
import it.unibo.scafi.cobalt.core.messages.networkService.NetworkServiceMessages.{AddNeighborForDeviceCmd, GetNeighborsCmd, NetworkServiceCommand, RemoveNeighborForDeviceCmd}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait NetworkServiceComponent { self: BasicCobaltIncarnation with NetworkServiceRepositoryComponent =>
  def service: Service = new Service

  class Service{

    def handle(cmd:NetworkServiceCommand) = cmd match {
      case GetNeighborsCmd(id) => getNeighbors(id)
      case AddNeighborForDeviceCmd(d1,d2) => addNeighborForDevice(d1,d2)
      case RemoveNeighborForDeviceCmd(d1,d2) => removeNeighborForDevice(d1,d2)
    }

    private def getNeighbors(deviceId: ID) : Future[Set[ID]] = repository.getNeighborsIdForDevice(deviceId)

    private def addNeighborForDevice(deviceId: ID, nbrId: ID): Future[ID] = repository.addNeighborForDevice(deviceId,nbrId)

    private def removeNeighborForDevice(deviceId: ID, nbrId: ID): Future[ID] = repository.removeNeighborForDevice(deviceId,nbrId)
  }
}
