package it.unibo.scafi.cobalt.core.messages.computingService

import it.unibo.scafi.cobalt.core.services.computingService.BasicCobaltIncarnation

/**
  * Created by tfarneti.
  */
object ComputingServiceMessages extends BasicCobaltIncarnation{

  case class ComputeNewState(id:ID)
}
