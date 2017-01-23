package it.unibo.scafi.cobalt.core.services.computingService

import it.unibo.scafi.core.Core
import it.unibo.scafi.incarnations.Incarnation

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ComputingRepositoryComponent { self:Incarnation =>
  def repository: Repository

  type STATE <: State

  trait Repository{

    def get(id: ID):Future[STATE]
    def set(id: ID)(state: STATE):Future[STATE]

    def getAll(id: Set[ID]): Future[Map[ID,Option[STATE]]]
  }

  trait State{
    val id: ID
    val export: EXPORT
  }
}

trait RedisComputingRepositoryComponent extends ComputingRepositoryComponent{ self:Incarnation =>
  override type ID = String
  override type STATE = StateImpl

  override def repository = new RedisRepository()

  class RedisRepository extends Repository{
    override def get(id: ID): Future[STATE] = ???

    override def set(id: ID)(state: STATE): Future[STATE] = ???

    override def getAll(id: Set[ID]): Future[Map[ID,Option[STATE]]] = ???
  }

  case class StateImpl(id: String,export: EXPORT) extends State
}