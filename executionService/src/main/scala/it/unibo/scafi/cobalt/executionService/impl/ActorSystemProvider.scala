package it.unibo.scafi.cobalt.executionService.impl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */
trait ActorSystemProvider {
  implicit val impSystem: ActorSystem
  implicit val impExecutor: ExecutionContextExecutor
  implicit val impMat: ActorMaterializer
}
