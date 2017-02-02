package it.unibo.scafi.cobalt.executionService.core

/**
  * Created by tfarneti.
  */
trait ExecutionServiceCore {
  type ID
  type EXPORT
  type STATE <: State

  trait State{
    val id: ID
    val export: EXPORT
  }
}
