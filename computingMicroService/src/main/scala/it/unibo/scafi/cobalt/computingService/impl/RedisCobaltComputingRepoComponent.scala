package it.unibo.scafi.cobalt.computingService.impl

import akka.util.ByteString
import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingRepositoryComponent}
import redis.{ByteStringFormatter, RedisClient}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */


trait RedisCobaltComputingRepoComponent extends ComputingRepositoryComponent { self: CobaltBasicIncarnation =>
  val redisClient : RedisClient

  override def repository = new RedisRepository(redisClient)

  implicit val stateSerializer = new ByteStringFormatter[StateImpl] {
    override def serialize(data: StateImpl): ByteString = {
      ByteString(data.id +"|"+ data.export)
    }

    override def deserialize(bs: ByteString): StateImpl = {
      val r = bs.utf8String.split('|').toList
      StateImpl(r(0), r(1))
    }
  }

  class RedisRepository(redisClient: RedisClient) extends Repository{
    override def get(id: String): Future[Option[StateImpl]] = redisClient.get[StateImpl](id)

    override def set(id: String, state: StateImpl): Future[Boolean] = redisClient.set[StateImpl](id, state)

    override def mGet(id: Set[String]): Future[Seq[Option[StateImpl]]] = redisClient.mget(id.toSeq: _*)
  }
}
