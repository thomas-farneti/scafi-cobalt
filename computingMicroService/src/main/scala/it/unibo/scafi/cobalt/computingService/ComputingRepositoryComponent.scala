package it.unibo.scafi.cobalt.computingService

import it.unibo.scafi.cobalt.core.incarnation.BasicCobaltIncarnation
import it.unibo.scafi.incarnations.Incarnation

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ComputingRepositoryComponent { self:Incarnation =>
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

trait BasicAbstractComputingRepositoryComponent extends ComputingRepositoryComponent{ self: BasicCobaltIncarnation =>
  override type STATE = StateImpl

  case class StateImpl(id: ID,export: EXPORT) extends State
}

trait ComputingRepositoryMockComponent extends BasicAbstractComputingRepositoryComponent { self:BasicCobaltIncarnation =>
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

