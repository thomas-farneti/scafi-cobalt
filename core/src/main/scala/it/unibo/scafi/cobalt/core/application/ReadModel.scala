package it.unibo.scafi.cobalt.core.application

/**
  * Created by tfarneti on 18/01/2017.
  */
trait ReadModel { self: AggregateApplicationComponent.dependencies =>
  case class GetAggregateApplication(id:String) extends Query
  case class GetAggregateApplicationResult(id:String,networkId:String)


  class AggregateApplicationReadModel{
    def query(getAggregateApplication: GetAggregateApplication): GetAggregateApplicationResult = {
      GetAggregateApplicationResult("","")
    }
  }

}
