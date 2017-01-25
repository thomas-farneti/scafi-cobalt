package it.unibo.scafi.cobalt.sensorManager.impl

import it.unibo.scafi.cobalt.sensorManager.core.SensorManagerRepositoryComponent
import redis.RedisClient

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait RedisSensorManagerRepoComponent extends SensorManagerRepositoryComponent{
  val redisClient : RedisClient
  override def repository = new RedisRepository(redisClient)

  class RedisRepository(redisClient: RedisClient) extends Repository{

    private def keyGen(deviceId:String, sensorName:String): String = s"sensors:$deviceId:$sensorName"

    override def getSensorValue(deviceId: String, sensorName: String): Future[Option[String]] = redisClient.get[String](keyGen(deviceId,sensorName))

    override def getSensorsValues(deviceId: String): Future[Option[Map[String, String]]] = ???
  }
}