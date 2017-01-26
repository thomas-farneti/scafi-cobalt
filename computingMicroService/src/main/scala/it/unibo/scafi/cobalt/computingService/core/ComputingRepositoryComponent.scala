package it.unibo.scafi.cobalt.computingService.core

import it.unibo.scafi.cobalt.core.incarnation.ScafiCobaltIncarnation
import it.unibo.scafi.incarnations.Incarnation

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ComputingRepositoryComponent { self: ComputingServiceCore =>
  def repository: Repository

  type STATE <: State

  trait Repository{
    def get(id: ID):Future[Option[STATE]]
    def set(id: ID, state: STATE):Future[Boolean]

    def mGet(id: Set[ID]): Future[Seq[Option[STATE]]]
  }

  trait State{
    val id: ID
    val export: EXPORT
  }
}

trait CobaltBasicIncarnation extends ComputingServiceCore{
  override type ID = String
  override type EXPORT = String
  override type STATE = StateImpl

  case class StateImpl(id: String,export: String) extends State
}

trait ComputingRepositoryMockComponentCobalt extends ComputingRepositoryComponent{ self: CobaltBasicIncarnation =>
  override def repository = new MockRepo()

  class MockRepo extends Repository{
    private val db = collection.mutable.Map("1" -> new STATE("1", new EXPORT))

    override def get(id: ID): Future[Option[STATE]] = {
      Future.successful(db.get(id))
    }

    override def set(id: ID, state: STATE): Future[Boolean] = {
      Future.successful({db.put(id,state);true})
    }

    override def mGet(id: Set[ID]): Future[Seq[Option[STATE]]] = {
      Future.successful(Seq(Some(new STATE("1", new EXPORT))))
    }
  }
}

