package it.unibo.scafi.cobalt.core.services.networkService

import it.unibo.scafi.cobalt.core.incarnation.BasicCobaltIncarnation
import it.unibo.scafi.incarnations.Incarnation

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait NetworkServiceRepositoryComponent { self: Incarnation =>
  def repository : NetworkServiceRepository

  trait NetworkServiceRepository{
    def getNeighborsIdForDevice(deviceId: ID): Future[Set[ID]]

    def addNeighborForDevice(deviceId: ID,nbrId: ID): Future[ID]

    def removeNeighborForDevice(deviceId: ID, nbrId: ID): Future[ID]
  }
}

trait NetworkServiceRepositoryMockComponent extends NetworkServiceRepositoryComponent{ self: BasicCobaltIncarnation =>
  class MockRepository extends NetworkServiceRepository{

    private val db:Map[String,collection.mutable.Set[String]] = Map("1"-> collection.mutable.Set("2","3"))

    override def getNeighborsIdForDevice(deviceId: String): Future[Set[String]] = Future.successful(db(deviceId).toSet)

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
  }
}