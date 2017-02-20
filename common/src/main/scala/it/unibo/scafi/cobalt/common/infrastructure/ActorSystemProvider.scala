package it.unibo.scafi.cobalt.common.infrastructure

import akka.actor.ActorSystem

/**
  * Created by tfarneti.
  */
trait ActorSystemProvider {
  implicit val impSystem: ActorSystem
}
