package it.unibo.scafi.cobalt.executionService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ExecutionRepositoryComponent { self: ExecutionServiceCore =>
  def repository: Repository

  trait Repository{
    def get(id: ID):Future[Option[STATE]]
    def set(id: ID, state: STATE):Future[Boolean]

    def mGet(id: Set[ID]): Future[Seq[Option[STATE]]]
  }
}





