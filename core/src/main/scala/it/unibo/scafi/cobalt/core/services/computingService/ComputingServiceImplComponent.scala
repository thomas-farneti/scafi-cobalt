package it.unibo.scafi.cobalt.core.services.computingService

import it.unibo.scafi.incarnations.{BasicAbstractIncarnation, Incarnation}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ComputingServiceComponent { self:ComputingRepositoryComponent with Incarnation =>
  def service: ComputingService

  trait ComputingService {
    def get(id: ID): Future[STATE]
  }
}

trait ComputingServiceImplComponent extends ComputingServiceComponent{ self: ComputingRepositoryComponent with Incarnation=>
  override def service = new ComputingServiceImpl

  class ComputingServiceImpl extends ComputingService{
    override def get(id: ID): Future[STATE] = ???
  }
}


object ApplicationLive {
//  val userServiceComponent = new ComputingServiceImplComponent with RedisComputingRepositoryComponent with BasicAbstractIncarnation

//  val userService = userServiceComponent.service

}