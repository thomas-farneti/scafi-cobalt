package it.unibo.scafi.cobalt.executionService.impl.scafi

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.ExecutionRepositoryComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ScafiMockExecutionRepositoryComponent extends ExecutionRepositoryComponent{ self: ScafiIncarnation with ExecutionContextProvider=>
  @transient private val singleton = new ScafiMockRepo
  override def repository = singleton

  class ScafiMockRepo extends Repository{
    private val db = collection.mutable.Map[String,Array[Byte]]()

    override def get(id: String): Future[Option[StateImpl]] = {
      Future{
        db.get(id).map(v => deserialise[StateImpl](v))
      }
    }

    override def set(id: String, state: StateImpl): Future[Boolean] = {
      Future{
        db.put(id,serialize[StateImpl](state))
        true
      }
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

    override def mGet(id: Set[String]): Future[Map[String, Option[StateImpl]]] = {
      Future(id.map(i => i -> (db.get(i) match {
        case Some(v) => Some(deserialise[StateImpl](v))
        case _ => None
      })).toMap)
    }
  }
}
