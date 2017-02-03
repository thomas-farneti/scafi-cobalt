package it.unibo.scafi.cobalt.ingestionService.core

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
    def updateSensorsValues(deviceId:String, sensorsValues: Map[String,String]): Future[Boolean] = {
      repository.setSensorsValues(deviceId,sensorsValues)
    }
  }
}

object IngestionServiceComponent{
  type dependencies = IngestionServiceRepositoryComponent
}

