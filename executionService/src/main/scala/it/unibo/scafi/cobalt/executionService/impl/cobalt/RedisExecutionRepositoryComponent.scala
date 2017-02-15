package it.unibo.scafi.cobalt.executionService.impl.cobalt

import akka.util.ByteString
import it.unibo.scafi.cobalt.executionService.core.ExecutionRepositoryComponent
import redis.{ByteStringFormatter, RedisClient}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */


trait RedisExecutionRepositoryComponent extends ExecutionRepositoryComponent { self: CobaltBasicIncarnation =>
  val redisClient: RedisClient
  override def repository = new RedisRepository(redisClient)

  class RedisRepository(redisClient: RedisClient) extends Repository{

    implicit val stateSerializer = new ByteStringFormatter[StateImpl] {
      override def serialize(data: StateImpl): ByteString = {
        ByteString(data.id +"|"+ data.export)
      }

      override def deserialize(bs: ByteString): StateImpl = {
        val r = bs.utf8String.split('|').toList
        StateImpl(r(0), r(1))
      }
    }

    override def get(id: String): Future[Option[StateImpl]] = redisClient.get[StateImpl](id)

    override def set(id: String, state: StateImpl): Future[Boolean] = redisClient.set[StateImpl](id, state)

    override def mGet(id: Set[String]): Future[Seq[Option[StateImpl]]] =
      if(id.isEmpty)
        Future.successful(Seq(None))
      else
        redisClient.mget(id.toSeq: _*)
  }
}
