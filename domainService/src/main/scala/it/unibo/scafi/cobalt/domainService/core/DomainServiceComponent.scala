package it.unibo.scafi.cobalt.domainService.core

import it.unibo.scafi.cobalt.common.domain.BoundingBox

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait DomainServiceComponent { self:DomainRepositoryComponent =>
  def service: Service = new Service

  class Service{
    def getNeighbors(deviceId: String) : Future[Set[String]] = repository.getNeighborsIdsForDevice(deviceId)

    def addNeighborForDevice(deviceId: String, nbrId: String): Future[String] = repository.addNeighborForDevice(deviceId,nbrId)

    def removeNeighborForDevice(deviceId: String, nbrId: String): Future[String] = repository.removeNeighborForDevice(deviceId,nbrId)

    def updatePosition(deviceId: String, latitude:String, longitude:String) : Future[Boolean] = repository.updatePosition(deviceId, latitude, longitude)

    def getNbrsSpatial(deviceId: String): Future[Set[String]] = repository.getNbrsSpatial(deviceId)

    def getDevicesByBoundingBox(boundingBox: BoundingBox): Future[Seq[String]] = repository.getDevicesByBoundingBox(boundingBox)
  }
}

