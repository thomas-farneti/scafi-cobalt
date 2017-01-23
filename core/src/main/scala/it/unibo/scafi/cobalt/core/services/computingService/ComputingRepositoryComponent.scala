package it.unibo.scafi.cobalt.core.services.computingService

import it.unibo.scafi.core.Core
import it.unibo.scafi.incarnations.{BasicAbstractIncarnation, Incarnation}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ComputingRepositoryComponent { self:BasicAbstractIncarnation =>
  def repository: Repository

  type STATE <: State

  trait Repository{
    def get(id: ID):Future[Option[STATE]]
    def set(id: ID, state: STATE):Future[STATE]

    def getAll(id: Set[ID]): Future[Map[ID,Option[STATE]]]
  }

  trait State{
    val id: ID
    val export: EXPORT
  }
}

trait BasicComputingRepositoryComponent extends ComputingRepositoryComponent{ self: BasicAbstractIncarnation =>
  override type STATE = StateImpl

  case class StateImpl(id: ID,export: EXPORT) extends State
}

trait MockComputingRepoCmp extends BasicComputingRepositoryComponent { self:BasicAbstractIncarnation =>
  override def repository = new MockRepo()

  class MockRepo extends Repository{
    private val db = collection.mutable.Map(1 -> new STATE(1, new EXPORT))

    override def get(id: ID): Future[Option[STATE]] = {
      Future.successful(db.get(id))
    }

    override def set(id: ID, state: STATE): Future[STATE] = {
      Future.successful(db.put(id,state).get)
    }

    override def getAll(id: Set[ID]): Future[Map[ID,Option[STATE]]] = {
      Future.successful(Map(id.head->Some(new STATE(1, new EXPORT))))
    }
  }
}

trait RedisComputingRepositoryComponent extends BasicComputingRepositoryComponent{ self:BasicAbstractIncarnation =>

  override def repository = new RedisRepository()

  class RedisRepository extends Repository{
    override def get(id: ID): Future[Option[STATE]] = ???

    override def set(id: ID, state: STATE): Future[STATE] = ???

    override def getAll(id: Set[ID]): Future[Map[ID,Option[STATE]]] = ???
  }
}