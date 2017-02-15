package it.unibo.scafi.cobalt.executionService.impl.scafi

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import it.unibo.scafi.cobalt.executionService.core.ExecutionRepositoryComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ScafiMockExecutionRepositoryComponent extends ExecutionRepositoryComponent{ self: ScafiIncarnation =>
  override def repository = new ScafiMockRepo

  class ScafiMockRepo extends Repository{
    private val db = collection.mutable.Map("1" -> serialize[StateImpl](StateImpl("1", new ExportImpl())))

    override def get(id: String): Future[Option[StateImpl]] = {
      Future.successful{
        db.get(id).map(v => deserialise[StateImpl](v))
      }
    }

    override def set(id: String, state: StateImpl): Future[Boolean] = {
      Future.successful({db.put(id,serialize[StateImpl](state));true})
    }

    override def mGet(id: Set[String]): Future[Seq[Option[StateImpl]]] = {
      Future.successful{
        id.map(i => db.get(i)).map{
          case Some(v) => Some(deserialise[StateImpl](v))
          case _ => None
        }.toSeq
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
  }
}
