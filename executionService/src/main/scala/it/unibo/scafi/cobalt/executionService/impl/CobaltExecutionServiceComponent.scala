package it.unibo.scafi.cobalt.executionService.impl

import it.unibo.scafi.cobalt.common.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.{CobaltBasicIncarnation, ExecutionGatewayComponent, ExecutionRepositoryComponent, ExecutionServiceComponent}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait CobaltExecutionServiceComponent extends ExecutionServiceComponent{ self : CobaltExecutionServiceComponent.dependencies =>
  override def service = new CobaltService

  class CobaltService() extends ComputingService {
    override def computeNewState(deviceId: String): Future[StateImpl] = {

      val myStateF = repository.get(deviceId)
      val mySensorF = gateway.sense(deviceId,"gps")

      for{
        nbrs <- gateway.getAllNbrsIds(deviceId)
        nbrsExports <- repository.mGet(nbrs)
        myState <- myStateF
        mySensor <- mySensorF

      }yield compute(deviceId,myState,mySensor,nbrsExports)

    }

    private def compute(id:String,myState: Option[STATE],mySensor: EXPORT, nbrsExports: Seq[Option[STATE]]): STATE ={
      StateImpl(id,nbrsExports.size.toString)
    }
  }
}

object CobaltExecutionServiceComponent {
  type dependencies = ExecutionRepositoryComponent with ExecutionGatewayComponent with CobaltBasicIncarnation with ExecutionContextProvider
}
