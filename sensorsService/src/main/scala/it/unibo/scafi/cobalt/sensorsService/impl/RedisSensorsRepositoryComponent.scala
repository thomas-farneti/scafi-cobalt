package it.unibo.scafi.cobalt.sensorsService.impl

import it.unibo.scafi.cobalt.sensorsService.core.SensorsRepositoryComponent
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait RedisSensorsRepositoryComponent extends SensorsRepositoryComponent{
  val redisClient : RedisClient
  override def repository = new RedisRepository(redisClient)

  class RedisRepository(redisClient: RedisClient) extends Repository{

    private def keyGen(deviceId:String): String = s"sensors:$deviceId"

    override def getSensorValue(deviceId: String, sensorName: String): Future[Option[Any]] = redisClient.hmget[String](keyGen(deviceId),sensorName).map(_.head)

    override def getSensorsValues(deviceId: String): Future[Map[String, Any]] = redisClient.hgetall[String](keyGen(deviceId))
  }
}