package it.unibo.scafi.cobalt.networkService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait NetworkServiceComponent { self:NetworkServiceRepositoryComponent =>
  def service: Service = new Service

  class Service{
    def getNeighbors(deviceId: String) : Future[Set[String]] = repository.getNeighborsIdsForDevice(deviceId)

    def addNeighborForDevice(deviceId: String, nbrId: String): Future[String] = repository.addNeighborForDevice(deviceId,nbrId)

    def removeNeighborForDevice(deviceId: String, nbrId: String): Future[String] = repository.removeNeighborForDevice(deviceId,nbrId)
  }
}
