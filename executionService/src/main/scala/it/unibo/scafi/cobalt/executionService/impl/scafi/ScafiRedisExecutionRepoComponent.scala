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
    implicit val stateSerializer = new ByteStringFormatter[StateImpl] {
      override def serialize(data: StateImpl): ByteString = {
        val res = serialize(data)
        res
      }

      override def deserialize(bs: ByteString): StateImpl = {
        deserialise[StateImpl](bs.toArray)
      }
    }

    override def get(id: String): Future[Option[StateImpl]] = redisClient.get[StateImpl](id)

    override def set(id: String, state: StateImpl): Future[Boolean] = redisClient.set[StateImpl](id, state)

    override def mGet(id: Set[String]): Future[Seq[Option[StateImpl]]] = {
      if(id.isEmpty)
        Future.successful(Seq(None))
      else
        redisClient.mget(id.toSeq: _*)
    }


    private def serialize[T <: StateImpl](value: T): Array[Byte] = {
      val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
      val oos = new ObjectOutputStream(stream)
      oos.writeObject(value)
      oos.close
      stream.toByteArray
    }

    private def deserialise[T <: StateImpl](bytes: Array[Byte]): T = {
      val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
      val value = ois.readObject.asInstanceOf[T]
      ois.close
      value
    }
  }

}

object ScafiRedisExecutionRepoComponent{
  type dependencies = ScafiIncarnation with RedisConfiguration with ActorSystemProvider
}
