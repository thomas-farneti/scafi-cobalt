package it.unibo.scafi.cobalt.executionService.impl.cobalt

import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.{ExecutionGatewayComponent, ExecutionRepositoryComponent, ExecutionServiceComponent}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait CobaltExecutionServiceComponent extends ExecutionServiceComponent{ self : CobaltExecutionServiceComponent.dependencies =>
  override def service = new CobaltService

  class CobaltService extends ExecutionService {
    override def execRound(deviceId: String): Future[StateImpl] = {

      val myStateF = repository.get(deviceId)
      val mySensorF = gateway.sense(deviceId,"gps")

      for{
        nbrs <- gateway.getAllNbrsIds(deviceId)
        nbrsExports <- repository.mGet(nbrs)
        myState <- myStateF
        mySensor <- mySensorF.map(_.toString)

      }yield compute(deviceId,myState,mySensor,nbrsExports)

    }

    private def compute(id:String,myState: Option[STATE],mySensor: EXPORT, nbrsExports: Seq[Option[STATE]]): STATE ={
      StateImpl(id,nbrsExports.size.toString)
    }

    override def fetchState(deviceId: String) = ???
  }
}

object CobaltExecutionServiceComponent {
  type dependencies = ExecutionRepositoryComponent with ExecutionGatewayComponent with CobaltBasicIncarnation with ExecutionContextProvider
}
