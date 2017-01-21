package it.unibo.scafi.cobalt.services.computingService

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

/**
  * Created by tfarneti.
  */
object ComputingMicroService extends App with Config {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()


}
