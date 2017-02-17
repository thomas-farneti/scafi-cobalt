package it.unibo.scafi.cobalt.executionService.impl.cobalt

import it.unibo.scafi.cobalt.executionService.core.ExecutionRepositoryComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ExecutionMockRepositoryComponent extends ExecutionRepositoryComponent{ self: CobaltBasicIncarnation =>
  override def repository = new MockRepo()

  class MockRepo extends Repository{
    private val db = collection.mutable.Map("1" -> "1")

    override def get(id: ID): Future[Option[String]] = {
      Future.successful(db.get(id))
    }

    override def set(id: ID, state: String): Future[Boolean] = {
      Future.successful({db.put(id,state);true})
    }

    override def mGet(id: Set[ID]): Future[Map[String,String]] = {
      Future.successful(Map("1" -> "1"))
    }
  }
}
