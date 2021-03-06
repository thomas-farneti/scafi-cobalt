package it.unibo.scafi.cobalt.executionService.impl.cobalt

import it.unibo.scafi.cobalt.executionService.core.ExecutionRepositoryComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait CobaltMockExecutionRepositoryComponent extends ExecutionRepositoryComponent{ self: CobaltBasicIncarnation =>
  private val repoInstance = new MockRepo
  override def repository = repoInstance

  class MockRepo extends Repository{
    private val db = collection.mutable.Map[String,String]()

    override def get(id: ID): Future[Option[String]] = {
      Future.successful(db.get(id))
    }

    override def set(id: ID, state: String): Future[Boolean] = {
      Future.successful({db.put(id,state);true})
    }

    override def mGet(id: Set[ID]): Future[Map[String,String]] = {
      Future.successful(id.flatMap(i => db.get(i).map(i -> _)).toMap)
    }
  }
}
