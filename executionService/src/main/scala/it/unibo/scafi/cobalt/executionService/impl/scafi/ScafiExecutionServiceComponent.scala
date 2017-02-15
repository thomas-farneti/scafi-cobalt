package it.unibo.scafi.cobalt.executionService.impl.scafi


import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.{ExecutionGatewayComponent, ExecutionRepositoryComponent, ExecutionServiceComponent}

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by tfarneti.
  */
trait ScafiExecutionServiceComponent extends ExecutionServiceComponent{ self: ScafiExecutionServiceComponent.dependencies =>
  def service = new ScafiExecutionService

  class ScafiExecutionService extends ExecutionService{
    override def execRound(deviceId: String): Future[StateImpl] = {

      val newState = createContext(deviceId).map{ c =>
        StateImpl(deviceId,new Gradient("source").round(c))
      }

      newState.map(s => repository.set(s.id,s))

      newState
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

      val lastExportF = repository.get(deviceId)

      val exportsF = for{
        nbrsIds <- nbrsIdsF
        nbrsExports <- repository.mGet(nbrsIds).map(_.flatten.map(s => s.id -> s.export).toMap)
        lastExport <- lastExportF.map{
          case Some(v) => Map(v.id -> v.export)
          case _ => Map()
        }
      }yield nbrsExports ++ lastExport


      for{
        exports <- exportsF
        localSensors <- localSensorsF
        nbrsSensors <- nbrsSensorsF
      }yield new ContextImpl(deviceId,exports,localSensors,nbrsSensors)

    }

  }

  class Gradient(val srcSensor: LSNS) extends AggregateProgram {
    def gradient(source: Boolean): Double =
      rep(Double.MaxValue){
        distance => mux(source) { 0.0 } {
          foldhood(Double.MaxValue)((x,y)=>if (x<y) x else y)(nbr{distance}+nbrvar[Double](NBR_RANGE_NAME))}}

    override def main(): Any = gradient(sense(srcSensor))
  }
}

object ScafiExecutionServiceComponent {
  type dependencies =  ExecutionGatewayComponent with ExecutionRepositoryComponent with ScafiIncarnation with ExecutionContextProvider
}