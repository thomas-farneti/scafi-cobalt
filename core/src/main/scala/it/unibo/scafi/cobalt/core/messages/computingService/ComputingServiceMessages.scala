package it.unibo.scafi.cobalt.core.messages.computingService

import it.unibo.scafi.incarnations.{BasicAbstractIncarnation, Incarnation}

/**
  * Created by tfarneti.
  */
object ComputingServiceMessages extends BasicAbstractIncarnation{

  case class ComputeNewState(id:ID)
}
