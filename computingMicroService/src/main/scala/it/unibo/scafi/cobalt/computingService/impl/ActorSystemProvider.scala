package it.unibo.scafi.cobalt.computingService.impl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */
trait ActorSystemProvider {
  implicit def impSystem: ActorSystem
  implicit def impExecutor: ExecutionContextExecutor
  implicit def impMat: ActorMaterializer
}
