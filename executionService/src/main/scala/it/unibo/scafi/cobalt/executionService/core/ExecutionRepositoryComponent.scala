package it.unibo.scafi.cobalt.executionService.core

import scala.concurrent.Future

import ScafiCobaltIncarnation._

/**
  * Created by tfarneti.
  */
trait ExecutionRepositoryComponent {
  def repository: Repository

  trait Repository{
    def get(id: ID):Future[Option[EXPORT]]
    def set(id: ID, state: EXPORT):Future[Boolean]

    def mGet(id: Set[ID]): Future[Map[ID,EXPORT]]
  }
}





