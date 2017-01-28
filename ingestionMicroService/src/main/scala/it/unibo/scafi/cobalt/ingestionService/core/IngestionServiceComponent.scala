package it.unibo.scafi.cobalt.ingestionService.core

import it.unibo.scafi.cobalt.core.messages.ingestionService.{UpdateSensorValueCmd, UpdateSensorsValues}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait IngestionServiceComponent { self : IngestionServiceComponent.dependencies =>
  def service = new IngestionService()

  class IngestionService{
    def updateSensorValue(deviceId:String,sensorName:String,sensorValue:String): Future[Boolean] = {
      repository.setSensorValue(deviceId,sensorName,sensorValue)
    }
    def updateSensorsValues(cmd: UpdateSensorsValues): Future[Boolean] = {
      repository.setSensorsValues(cmd.deviceId,cmd.sensorsValues)
    }
  }
}

object IngestionServiceComponent{
  type dependencies = IngestionServiceRepositoryComponent
}

