package it.unibo.scafi.cobalt.sensorManager.impl

import it.unibo.scafi.cobalt.sensorManager.core.SensorManagerRepositoryComponent
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait RedisSensorManagerRepoComponent extends SensorManagerRepositoryComponent{
  val redisClient : RedisClient
  override def repository = new RedisRepository(redisClient)

  class RedisRepository(redisClient: RedisClient) extends Repository{

    private def keyGen(deviceId:String): String = s"sensors:$deviceId"

    override def getSensorValue(deviceId: String, sensorName: String): Future[Option[String]] = redisClient.hmget[String](keyGen(deviceId),sensorName).map(_.head)

    override def getSensorsValues(deviceId: String): Future[Option[Map[String, String]]] = redisClient.hgetall[String](keyGen(deviceId)).map{
      case a if a.isEmpty => None
      case a => Some(a)
    }
  }
}