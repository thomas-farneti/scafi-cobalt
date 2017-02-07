package it.unibo.scafi.cobalt.common

import akka.actor.ActorSystem

/**
  * Created by tfarneti.
  */
trait ActorSystemProvider {
  implicit val impSystem: ActorSystem
}
