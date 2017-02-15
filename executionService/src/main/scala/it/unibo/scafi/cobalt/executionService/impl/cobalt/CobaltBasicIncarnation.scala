package it.unibo.scafi.cobalt.executionService.impl.cobalt

import it.unibo.scafi.cobalt.executionService.core.ExecutionServiceCore

/**
  * Created by tfarneti.
  */
trait CobaltBasicIncarnation extends ExecutionServiceCore{
  override type ID = String
  override type EXPORT = String
  override type LSNS = String
  override type NSNS = String
  override type STATE = StateImpl

  case class StateImpl(id: String,export: String) extends State
}
