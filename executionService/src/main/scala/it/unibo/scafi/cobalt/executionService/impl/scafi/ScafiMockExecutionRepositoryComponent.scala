package it.unibo.scafi.cobalt.executionService.impl.scafi

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.ExecutionRepositoryComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
//trait ScafiMockExecutionRepositoryComponent extends ExecutionRepositoryComponent{ self: ScafiIncarnation with ExecutionContextProvider=>
//  @transient private val singleton = new ScafiMockRepo
//  override def repository = singleton
//
//  class ScafiMockRepo extends Repository{
//    private val db = collection.mutable.Map[String,Array[Byte]]()
//
//    override def get(id: String): Future[Option[ExportImpl]] = {
//      Future{
//        db.get(id).map(v => deserialise[ExportImpl](v))
//      }
//    }
//
//    override def set(id: String, state: ExportImpl): Future[Boolean] = {
//      Future{
//        db.put(id,serialize[ExportImpl](state))
//        true
//      }
//    }
//
//    override def mGet(id: Set[String]): Future[Map[String,ExportImpl]] = {
//      Future{
//        id.flatMap(i => db.get(i).map(r=> i -> deserialise[ExportImpl](r))).toMap
//      }
//    }
//
//    private def serialize[T <: ExportImpl](value: T): Array[Byte] = {
//      val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
//      val oos = new ObjectOutputStream(stream)
//      oos.writeObject(value)
//      oos.close
//      stream.toByteArray
//    }
//
//    private def deserialise[T <: ExportImpl](bytes: Array[Byte]): T = {
//      val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
//      val value = ois.readObject.asInstanceOf[T]
//      ois.close
//      value
//    }
//  }
//}

trait ScafiMockExecutionRepositoryComponent extends ExecutionRepositoryComponent{ self: ScafiIncarnation with ExecutionContextProvider=>
  @transient private val singleton = new ScafiMockRepo
  override def repository = singleton

  class ScafiMockRepo extends Repository{
    private val db = collection.mutable.Map[String,ExportImpl]()

    override def get(id: String): Future[Option[ExportImpl]] = {
      Future{
        db.get(id)
      }
    }

    override def set(id: String, state: ExportImpl): Future[Boolean] = {
      Future{
        db.put(id,state)
        true
      }
    }

    override def mGet(id: Set[String]): Future[Map[String,ExportImpl]] = {
      Future{
        id.flatMap(i => db.get(i).map(r=> i -> r)).toMap
      }
    }
  }
}
