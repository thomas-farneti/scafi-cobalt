package it.unibo.scafi.cobalt.core.application

/**
  * Created by tfarneti on 18/01/2017.
  */
trait AggregateApplicationMessages { self: AggregateApplicationComponent.dependencies =>
  case class CreateNewApplication(id:String) extends Command
  case class DeleteApplication(id:String) extends Command

  case class StartApplication(id: String) extends Command
  case class StopApplication(id:String) extends Command
}
