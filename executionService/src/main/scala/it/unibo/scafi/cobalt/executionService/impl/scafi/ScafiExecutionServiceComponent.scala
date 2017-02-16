package it.unibo.scafi.cobalt.executionService.impl.scafi


import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.{ExecutionGatewayComponent, ExecutionRepositoryComponent, ExecutionServiceComponent}

import scala.collection.Map
import scala.concurrent.{Await, Future}

/**
  * Created by tfarneti.
  */
trait ScafiExecutionServiceComponent extends ExecutionServiceComponent{ self: ScafiExecutionServiceComponent.dependencies =>
  def service = new ScafiExecutionService

  class ScafiExecutionService extends ExecutionService{

    override def execRound(deviceId: String): Future[StateImpl] = {

      val contextF = createContext(deviceId)

      val stateF = for{
        ctx <- contextF
        p <- Future(println(ctx))
      } yield StateImpl(deviceId,new HopGradient("source").round(ctx))

      val a = for{
        state <- stateF
        res <- repository.set(state.id,state)
      }yield res

      import scala.concurrent.duration._
      Await.result(a, 1 second)

      stateF
    }

    override def fetchState(deviceId: String): Future[StateImpl] = {
      Future.successful(new StateImpl(deviceId,factory.emptyExport()))
    }

    private def createContext(deviceId:String): Future[ContextImpl] = {
      val nbrsIdsF = gateway.getAllNbrsIds(deviceId)
      val localSensorsF = gateway.localSensorsSense(deviceId)

      val nbrsSensorsF =  for{
        nbrsIds <- nbrsIdsF
        nbrSensors <- gateway.nbrSensorsSense(nbrsIds)
      }yield nbrSensors

      val lastExportF = repository.get(deviceId).map {
        case Some(v) => Map(v.id -> v.export)
        case _ => Map(deviceId -> factory.emptyExport())
      }

      val exportsF = for{
        nbrsIds <- nbrsIdsF
        nbrsExports <- repository.mGet(nbrsIds).map(_.map(i => i._2 match {
          case Some(v) => i._1 -> v.export
          case _ => i._1 -> factory.emptyExport()
        }).toMap)
        lastExport <- lastExportF
      }yield nbrsExports ++ lastExport

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