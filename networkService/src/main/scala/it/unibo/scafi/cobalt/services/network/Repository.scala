package it.unibo.scafi.cobalt.services.network

import redis.RedisClient

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
class Repository extends Config {
  private val redis = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))

  def getNeighborsIdForDevice(deviceId: String): Future[Set[String]] = redis.smembers[String](deviceIdToRedisKey(deviceId)).map[Set[String]](_.toSet)

  def addNeighborForDevice(deviceId: String, nbrId: String): Future[Boolean] = redis.sadd[String](deviceIdToRedisKey(deviceId),nbrId).map[Boolean] {
    case 1 => true
    case _ => false
  }

  def removeNeighborForDevice(deviceId: String, nbrId: String): Future[Boolean] = redis.srem[String](deviceIdToRedisKey(deviceId),nbrId).map[Boolean] {
    case 1 => true
    case _ => false
  }

  private def deviceIdToRedisKey(id: String): String = s"netSvc:id:${id}"
}
