package it.unibo.scafi.cobalt.domainService.core

import it.unibo.scafi.cobalt.common.domain.BoundingBox

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait DomainRepositoryComponent {
  def repository : Repository

  trait Repository{
    def getNbrsSpatial(deviceId: String): Future[Set[String]]

    def updatePosition(deviceId: String, latitude: Double, longitude: Double) : Future[Boolean]

    def getNeighborsIdsForDevice(deviceId: String): Future[Set[String]]

    def addNeighborForDevice(deviceId: String,nbrId: String): Future[String]

    def removeNeighborForDevice(deviceId: String, nbrId: String): Future[String]

    def getDevicesByBoundingBox(boundingBox: BoundingBox) : Future[Seq[String]]
  }
}

trait DomainRepositoryMockComponent extends DomainRepositoryComponent{
  def repository = new MockRepository()

  class MockRepository extends Repository{

    private val db:Map[String,collection.mutable.Set[String]] = Map("1"-> collection.mutable.Set("2","3"))

    override def getNeighborsIdsForDevice(deviceId: String): Future[Set[String]] = Future.successful(db(deviceId).toSet)

    override def addNeighborForDevice(deviceId: String, nbrId: String): Future[String] = if (db(deviceId).add(nbrId)) {
      Future.successful(deviceId)
    } else {
      Future.failed(new Exception)
    }

    override def removeNeighborForDevice(deviceId: String, nbrId: String): Future[String] = if (db(deviceId).remove(nbrId)) {
      Future.successful(deviceId)
    } else {
      Future.failed(new Exception)
    }

    override def updatePosition(deviceId: String, latitude: Double, longitude: Double): Future[Boolean] = ???

    override def getNbrsSpatial(deviceId: String): Future[Set[String]] = ???

    override def getDevicesByBoundingBox(boundingBox: BoundingBox): Future[Seq[String]]  = Future.successful(Seq())
  }
}