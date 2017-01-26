package it.unibo.scafi.cobalt.networkService.impl

import it.unibo.scafi.cobalt.networkService.core.NetworkServiceRepositoryComponent
import redis.RedisClient

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by tfarneti.
  */
trait RedisNetworkServiceRepoComponent extends NetworkServiceRepositoryComponent{
  val redisClient : RedisClient
  implicit val ec:ExecutionContext

  override def repository = new RedisRepository(redisClient)

  class RedisRepository(redisClient:RedisClient) extends Repository {
    private def deviceIdToRedisKey(id: String): String = s"netSvc:id:$id"

    override def getNeighborsIdsForDevice(deviceId: String): Future[Set[String]] = redisClient.smembers[String](deviceIdToRedisKey(deviceId)).map(_.toSet[String])

    override def addNeighborForDevice(deviceId: String, nbrId: String): Future[String] = redisClient.sadd(deviceIdToRedisKey(deviceId),nbrId).map[String](_ => deviceId)

    override def removeNeighborForDevice(deviceId: String, nbrId: String): Future[String] = ???
  }
}
