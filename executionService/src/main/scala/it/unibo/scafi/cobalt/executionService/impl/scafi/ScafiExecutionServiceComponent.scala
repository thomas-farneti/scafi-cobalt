package it.unibo.scafi.cobalt.executionService.impl.scafi

import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.{ExecutionGatewayComponent, ExecutionRepositoryComponent, ExecutionServiceComponent}

import scala.collection.Map
import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ScafiExecutionServiceComponent extends ExecutionServiceComponent{ self: ScafiExecutionServiceComponent.dependencies =>
  def service = new ScafiExecutionService

  class ScafiExecutionService extends ExecutionService{

    override def execRound(deviceId: String): Future[ExportImpl] = {

      val contextF = createContext(deviceId)

      val stateF = for{
        ctx <- contextF
        //p <- Future(println(ctx))
        state <- Future(new HopGradient("source").round(ctx))
        res <- repository.set(deviceId,state)
      } yield state

      stateF
    }

    override def fetchState(deviceId: String): Future[ExportImpl] = ???

    private def createContext(deviceId:String): Future[ContextImpl] = {
      val nbrsIdsF = gateway.getAllNbrsIds(deviceId)
      val localSensorsF = gateway.localSensorsSense(deviceId)

//      val nbrsSensorsF =  for{
//        nbrsIds <- nbrsIdsF
//        nbrSensors <- gateway.nbrSensorsSense(nbrsIds)
//      }yield nbrSensors

      val lastExportF = repository.get(deviceId).map( _.map( v => deviceId -> v).toMap)

      //val nbrExportsF = nbrsIdsF.flatMap( repository.mGet(_)).map( _.values.flatten.map(i => i.id -> i.export).toMap)
      val nbrExportsF = nbrsIdsF.flatMap( repository.mGet(_))

      val exportsF = for{
        last <- lastExportF
        nbrs <- nbrExportsF

      }yield last ++ nbrs

      for{
        exports <- exportsF
        localSensors <- localSensorsF
        //nbrsSensors <- nbrsSensorsF
      }yield new ContextImpl(deviceId,exports,localSensors,Map[NSNS,Map[ID,Any]]())//nbrsSensors)

    }

  }

  class HopGradient(val srcSensor: LSNS) extends AggregateProgram {
    def hopGradient(source: Boolean): Int = {
      rep(1000){
        hops => { mux(source) { 0 } { 1+minHood[Int](nbr[Int]{ hops }) } }
      }
    }

    override def main(): Any = hopGradient(sense(srcSensor))
  }

}

object ScafiExecutionServiceComponent {
  type dependencies =  ExecutionGatewayComponent with ExecutionRepositoryComponent with ScafiIncarnation with ExecutionContextProvider
}