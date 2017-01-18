package it.unibo.scafi.cobalt.backend.applicationService

import java.util.UUID

import akka.actor.Actor
import it.unibo.scafi.cobalt.api.applicationService.ApplicationServiceComponent.{ExecNewApp, StopApp}


/**
  * Created by tfarneti on 12/01/2017.
  */
class ApplicationServiceActor extends Actor {

  override def receive = {
    case ExecNewApp =>
      val appId = UUID.randomUUID().toString
      context.actorOf(AggregateApplicationActor.props(appId),name = appId)
    case StopApp =>

  }
}
