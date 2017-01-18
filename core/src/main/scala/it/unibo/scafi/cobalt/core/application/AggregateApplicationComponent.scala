package it.unibo.scafi.cobalt.core.application

import it.unibo.scafi.cobalt.core.messages.Messages


/**
  * Created by tfarneti on 18/01/2017.
  */
trait AggregateApplicationComponent extends AggregateApplicationMessages with
  ReadModel { self: AggregateApplicationComponent.dependencies =>
  class AggregateApplication(id:String){
    type aggregateProgram
    var _networkId : String = ""

    def changeNetwork(networkId: String): Unit = {
      _networkId = networkId
    }
  }

  class AggregateApplicationService{
    def handle(cmd: CreateNewApplication){}
    def handle(cmd: DeleteApplication){}

    def handle(cmd: StartApplication){}
    def handle(cmd: StopApplication){}
  }
}

object AggregateApplicationComponent{
  type dependencies = Messages
}
