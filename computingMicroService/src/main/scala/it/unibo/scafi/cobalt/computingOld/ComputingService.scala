package it.unibo.scafi.cobalt.computingOld

import it.unibo.scafi.core.{Core, Engine}
import scala.collection.mutable.{ Map => MMap }

/**
  * Created by tfarneti on 05/01/2017.
  */
trait ComputingService { self: ComputingService.Dependency =>

  class ComputingWorker(aggregateExecutor: ExecutionTemplate){

    def computeExport(selfId: ID, selfExport: EXPORT, nbrExports: Map[ID,EXPORT], sensorValues : MMap[LSNS, Any], nbrSensorValues: MMap[NSNS, MMap[ID, Any]]) =  {

      // Query local sensor to update sensor values
      //updateSensorValues()

      val exports = nbrExports + (selfId -> selfExport)

      val context = new ContextImpl(
        selfId,
        exports,
        sensorValues,
        nbrSensorValues)

      val exp = aggregateExecutor.round(context)
      exp

      //PropagateExportToNeighbors(export)

      //executeActuators(export.root())
    }
  }

}

object ComputingService{
  type Dependency = Core with Engine
}
