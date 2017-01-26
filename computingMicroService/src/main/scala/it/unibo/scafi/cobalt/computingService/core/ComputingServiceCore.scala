package it.unibo.scafi.cobalt.computingService.core

/**
  * Created by tfarneti.
  */
trait ComputingServiceCore {
  type ID
  type EXPORT
  type STATE <: State

  trait State{
    val id: ID
    val export: EXPORT
  }
}
