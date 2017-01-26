package it.unibo.scafi.cobalt.computingService.impl

import akka.util.ByteString
import it.unibo.scafi.cobalt.computingService.core.BasicAbstractComputingRepositoryComponent
import it.unibo.scafi.cobalt.core.incarnation.BasicCobaltIncarnation
import redis.{ByteStringFormatter, RedisClient}

import scala.concurrent.Future
import scala.util.Success

/**
  * Created by tfarneti.
  */


trait RedisComputingRepositoryImpl extends BasicAbstractComputingRepositoryComponent{ self:BasicCobaltIncarnation =>
  val redisClient : RedisClient

  override def repository = new RedisRepository(redisClient)

  implicit val stateSerializer = new ByteStringFormatter[STATE] {
    override def serialize(data: StateImpl): ByteString = ???

    override def deserialize(bs: ByteString): StateImpl = ???
  }

  class RedisRepository(redisClient: RedisClient) extends Repository{
    override def get(id: ID): Future[Option[STATE]] = redisClient.get[STATE](id)

    override def set(id: ID, state: STATE): Future[Boolean] = redisClient.set[STATE](id, state)

    override def mGet(id: Set[ID]): Future[Seq[Option[STATE]]] = redisClient.mget(id.toSeq: _*)
  }
}
