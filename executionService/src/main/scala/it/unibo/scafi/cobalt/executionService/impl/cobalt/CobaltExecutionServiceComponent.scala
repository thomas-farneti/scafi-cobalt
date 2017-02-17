package it.unibo.scafi.cobalt.executionService.impl.cobalt

import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.{ExecutionGatewayComponent, ExecutionRepositoryComponent, ExecutionServiceComponent}

import scala.collection.Map
import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait CobaltExecutionServiceComponent extends ExecutionServiceComponent{ self : CobaltExecutionServiceComponent.dependencies =>
  override def service = new CobaltService

  class CobaltService extends ExecutionService {
    override def execRound(deviceId: String): Future[String] = {
      val nbrsF = gateway.getAllNbrsIds(deviceId)

      val localSensorsF = gateway.localSensorsSense(deviceId)

      val nbrsSensorsF = for{
        nbrsId <- nbrsF
        nbrSensors <- gateway.nbrSensorsSense(nbrsId)
      } yield nbrSensors

      val lastExportF = repository.get(deviceId).map( _.map( v => deviceId -> v).toMap)

      val nbrExportsF = nbrsF.flatMap( repository.mGet(_))

      val exportsF = for{
        last <- lastExportF
        nbrs <- nbrExportsF
      }yield last ++ nbrs

      for{
        exports <- exportsF
        localSensors <- localSensorsF
        nbrsSensors <- nbrsSensorsF
        exp <- Future(round(deviceId, exports,localSensors,nbrsSensors))
        db <- repository.set(deviceId,exp)
      }yield exp
    }

    private def round(deviceId: String,exports:Map[String,String],localSensors: Map[String,Any], nbrSensors: Map[NSNS,Map[ID,Any]]): String ={
      if(localSensors("source").asInstanceOf[Boolean]){
        "0"
      }else{
        val values = exports.values.map(_.toInt)

        if(values.nonEmpty){
          ""+(values.min+1)
        }else{
          ""+10000
        }
      }
    }

    override def fetchState(deviceId: String) = ???
  }
}

object CobaltExecutionServiceComponent {
  type dependencies = ExecutionRepositoryComponent with ExecutionGatewayComponent with CobaltBasicIncarnation with ExecutionContextProvider
}
