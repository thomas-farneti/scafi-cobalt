package it.unibo.scafi.cobalt.state.redisImpl

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import com.redis._
import it.unibo.scafi.cobalt.state.StateStorage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by tfarneti on 04/01/2017.
  */
trait RedisStateStorage extends StateStorage { self : StateStorage.Dependency=>

  val redisClient = new RedisClient("localhost",6379)

  override def store(key: ID)(state: EXPORT) = {
    redisClient.set(key,Serializer.serialize(state))
  }


  override def getAsync(key: ID) = Future{
    this.get(key)
  }

  import com.redis.serialization.Parse.Implicits.parseByteArray
  override def get(key: ID) = redisClient.get[Array[Byte]](key) match {
    case Some (a) => Some(Serializer.deserialize[EXPORT](a))
    case _ => None
  }
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


