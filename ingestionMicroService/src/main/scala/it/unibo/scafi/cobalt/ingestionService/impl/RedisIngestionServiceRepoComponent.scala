package it.unibo.scafi.cobalt.ingestionService.impl

import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceRepositoryComponent
import redis.RedisClient

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait RedisIngestionServiceRepoComponent extends IngestionServiceRepositoryComponent{
  val redisClient : RedisClient
  override def repository = new RedisRepository(redisClient)

  class RedisRepository(redisClient: RedisClient) extends Repository{
    override def setSensorValue(deviceId: String, sensorName: String, sensorValue: String): Future[Boolean] = {
      redisClient.hmset(keyGen(deviceId),Map(sensorName -> sensorValue))
    }

    override def setSensorsValues(deviceId: String, sensorsValues: Map[String, String]): Future[Boolean] = {
      redisClient.hmset(keyGen(deviceId),sensorsValues)
    }

    private def keyGen(deviceId:String): String = s"sensors:$deviceId"
  }
}