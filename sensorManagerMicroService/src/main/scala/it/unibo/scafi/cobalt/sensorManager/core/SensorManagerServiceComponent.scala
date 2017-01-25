package it.unibo.scafi.cobalt.sensorManager.core

import it.unibo.scafi.cobalt.core.messages.ingestionService.{UpdateSensorValueCmd, UpdateSensorsValues}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait SensorManagerServiceComponent { self : SensorManagerServiceComponent.dependencies =>
  def service = new SensorManagerService()

  class SensorManagerService{
    def getSensorValue(deviceId:String,sensorName:String): Future[Option[String]] = {
      repository.getSensorValue(deviceId,sensorName)
    }
    def getSensorsValues(deviceId:String): Future[Option[Map[String,String]]] = {
      repository.getSensorsValues(deviceId)
    }
  }
}

object SensorManagerServiceComponent{
  type dependencies = SensorManagerRepositoryComponent
}

