package it.unibo.scafi.cobalt.common.infrastructure

import akka.stream.ActorMaterializer

/**
  * Created by tfarneti.
  */
trait ActorMaterializerProvider { self : ActorSystemProvider=>
  implicit val impmaterializer : ActorMaterializer
}
