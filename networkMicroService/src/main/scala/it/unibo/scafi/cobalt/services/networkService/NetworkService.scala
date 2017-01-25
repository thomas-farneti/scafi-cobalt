package it.unibo.scafi.cobalt.services.networkService

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import redis.RedisClient

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by tfarneti.
  */
trait NetworkRepository{
  def getNeighborsIdForDevice(deviceId: String): Future[Set[String]]

  def addNeighborForDevice(deviceId: String,nbrId: String): Future[Boolean]

  def removeNeighborForDevice(deviceId: String, nbrId: String): Future[Boolean]
}


class NetworkRepoRedisImpl(implicit actorSystem: ActorSystem, ec: ExecutionContext) extends NetworkRepository with Config {
  private val redis = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))

  def getNeighborsIdForDevice(deviceId: String): Future[Set[String]] = redis.smembers[String](deviceIdToRedisKey(deviceId)).map[Set[String]](_.toSet)

  def addNeighborForDevice(deviceId: String,nbrId: String): Future[Boolean] = redis.sadd[String](deviceIdToRedisKey(deviceId),nbrId).map[Boolean] {
    case 1 => true
    case _ => false
  }

  def removeNeighborForDevice(deviceId: String, nbrId: String): Future[Boolean] = redis.srem[String](deviceIdToRedisKey(deviceId),nbrId).map[Boolean] {
    case 1 => true
    case _ => false
  }

  private def deviceIdToRedisKey(id: String): String = s"netSvc:id:${id}"
}

class NetworkService(repo:NetworkRepository){

  def getNeighbors(deviceId: String) : Future[Set[String]] = {
    repo.getNeighborsIdForDevice(deviceId)
  }

  def addNeighborForDevice(deviceId: String, nbrId: String): Future[Boolean] = repo.addNeighborForDevice(deviceId,nbrId)

  def removeNeighborForDevice(deviceId: String, nbrId: String): Future[Boolean] = repo.removeNeighborForDevice(deviceId,nbrId)

}

class Router(service: NetworkService)(implicit actorSystem: ActorSystem, ec: ExecutionContext) extends Protocols{
  val routes = (path("neighbors" / Segment) & get){ id =>
    complete{
      service.getNeighbors(id).map[ToResponseMarshallable] {
        a => OK -> a
      }
    }
  }
}