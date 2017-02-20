package it.unibo.scafi.cobalt.executionService.impl.scafi

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import akka.util.ByteString
import it.unibo.scafi.cobalt.common.infrastructure.ActorSystemProvider
import it.unibo.scafi.cobalt.executionService.core.ExecutionRepositoryComponent
import it.unibo.scafi.cobalt.executionService.impl.RedisConfiguration
import redis.{ByteStringFormatter, RedisClient}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */


trait ScafiRedisExecutionRepoComponent extends ExecutionRepositoryComponent {self: ScafiRedisExecutionRepoComponent.dependencies =>
  val redisClient: RedisClient

  override def repository = new RedisRepository

  class RedisRepository extends Repository {
    override def get(id: String): Future[Option[ExportImpl]] = ???

    override def set(id: String, state: ExportImpl): Future[Boolean] = ???

    override def mGet(id: Set[String]): Future[Map[String, ExportImpl]] = ???
  }

}

object ScafiRedisExecutionRepoComponent{
  type dependencies = ScafiIncarnation with RedisConfiguration with ActorSystemProvider
}
