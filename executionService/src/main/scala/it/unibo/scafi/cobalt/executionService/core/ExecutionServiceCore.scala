package it.unibo.scafi.cobalt.executionService.core

/**
  * Created by tfarneti.
  */
trait ExecutionServiceCore{
  type ID
  type EXPORT
  type LSNS
  type NSNS

  type STATE <: State

  trait State extends Serializable{
    val id: ID
    val export: EXPORT
  }
}

