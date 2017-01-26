package it.unibo.scafi.cobalt.computingService.impl

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import akka.util
import akka.util.ByteString
import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingRepositoryComponent}
import it.unibo.scafi.cobalt.core.incarnation.ScafiCobaltIncarnation
import redis.{ByteStringFormatter, RedisClient}

import scala.concurrent.Future
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.util.Try

/**
  * Created by tfarneti.
  */


trait RedisComputingRepositoryImplScafi extends ComputingRepositoryComponent { self: CobaltBasicIncarnation with ScafiCobaltIncarnation =>
  val redisClient : RedisClient

  override def repository = new RedisRepository(redisClient)

  implicit val stateSerializer = new ByteStringFormatter[StateImpl] {
    override def serialize(data: StateImpl): ByteString = {
      val v = Try(Serializer.serialize(factory.emptyExport()))

      val res = ByteString.fromArray(Serializer.serialize(data))
      Console.printf(res.toString())
      res
    }

    override def deserialize(bs: ByteString): StateImpl = {
      Serializer.deserialize[StateImpl](bs.toArray)
    }
  }

  class RedisRepository(redisClient: RedisClient) extends Repository{
    override def get(id: String): Future[Option[StateImpl]] = redisClient.get[StateImpl](id)

    override def set(id: String, state: StateImpl): Future[Boolean] = redisClient.set[StateImpl](id, state)

    override def mGet(id: Set[String]): Future[Seq[Option[StateImpl]]] = redisClient.mget(id.toSeq: _*)
  }

  object Serializer {

    def serialize[T <: Serializable](obj: T): Array[Byte] = {
      val byteOut = new ByteArrayOutputStream()
      val objOut = new ObjectOutputStream(byteOut)
      objOut.writeObject(obj)
      objOut.close()
      byteOut.close()
      byteOut.toByteArray
    }

    def deserialize[T <: Serializable](bytes: Array[Byte]): T = {
      val byteIn = new ByteArrayInputStream(bytes)
      val objIn = new ObjectInputStream(byteIn)
      val obj = objIn.readObject().asInstanceOf[T]
      byteIn.close()
      objIn.close()
      obj
    }
  }
}
