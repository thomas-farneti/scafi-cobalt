package it.unibo.scafi.cobalt.networkService.impl

import it.unibo.scafi.cobalt.networkService.core.NetworkServiceRepositoryComponent
import redis.RedisClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by tfarneti.
  */
trait RedisNetworkServiceRepoComponent extends NetworkServiceRepositoryComponent{
  val redisClient : RedisClient

  override def repository = new RedisRepository(redisClient)

  class RedisRepository(redisClient:RedisClient) extends Repository {
    private def deviceIdToRedisKey(id: String): String = s"netSvc:id:$id"

    override def getNeighborsIdsForDevice(deviceId: String): Future[Set[String]] = redisClient.smembers[String](deviceIdToRedisKey(deviceId)).map(_.toSet[String])

    override def addNeighborForDevice(deviceId: String, nbrId: String): Future[String] = redisClient.sadd(deviceIdToRedisKey(deviceId),nbrId).map[String](_ => deviceId)

    override def removeNeighborForDevice(deviceId: String, nbrId: String): Future[String] = ???

    override def getNbrsSpatial(deviceId: String): Future[Set[String]] = redisClient.geoRadiusByMember("netSvc:spatial",deviceId,100).map(_.toSet)

    override def updatePosition(deviceId: String, latitude: String, longitude: String): Future[Boolean] = redisClient.geoAdd("netSvc:spatial",latitude.toDouble,longitude.toDouble,deviceId).map(_ => true)
  }
}
