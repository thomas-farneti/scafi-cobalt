package it.unibo.scafi.cobalt.executionService.impl.cobalt

import akka.util.ByteString
import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.ExecutionRepositoryComponent
import redis.{ByteStringFormatter, RedisClient}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */


trait CobaltRedisExecutionRepositoryComponent extends ExecutionRepositoryComponent { self: CobaltBasicIncarnation with ExecutionContextProvider=>
  val redisClient: RedisClient
  override def repository = new RedisRepository(redisClient)

  class RedisRepository(redisClient: RedisClient) extends Repository{

    override def get(id: String): Future[Option[String]] = redisClient.get[String](id)

    override def set(id: String, export: String): Future[Boolean] = redisClient.set(id,export)

    override def mGet(id: Set[String]): Future[Map[String, String]] = {
      if(id.isEmpty){
        Future.successful(Map())
      }
      else{
        redisClient.mget[String](id.toSeq: _*).map(v => Map(id.zip(v).toArray: _*).collect { case (key, Some(value)) => (key, value) })
      }
    }
  }
}
