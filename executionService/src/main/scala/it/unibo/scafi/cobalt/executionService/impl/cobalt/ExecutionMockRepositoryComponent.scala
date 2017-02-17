package it.unibo.scafi.cobalt.executionService.impl.cobalt

import it.unibo.scafi.cobalt.executionService.core.ExecutionRepositoryComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ExecutionMockRepositoryComponent extends ExecutionRepositoryComponent{ self: CobaltBasicIncarnation =>
  override def repository = new MockRepo()

  class MockRepo extends Repository{
    private val db = collection.mutable.Map("1" -> new STATE("1", new EXPORT))

    override def get(id: ID): Future[Option[STATE]] = {
      Future.successful(db.get(id))
    }

    override def set(id: ID, state: STATE): Future[Boolean] = {
      Future.successful({db.put(id,state);true})
    }

    override def mGet(id: Set[ID]): Future[Set[STATE]] = {
      Future.successful(Set(new STATE("1", new EXPORT)))
    }
  }
}
