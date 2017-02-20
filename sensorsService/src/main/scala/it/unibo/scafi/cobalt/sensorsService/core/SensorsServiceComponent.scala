package it.unibo.scafi.cobalt.sensorsService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait SensorsServiceComponent { self : SensorsServiceComponent.dependencies =>
  def service = new SensorsService()

  class SensorsService{
    def getSensorValue(deviceId:String,sensorName:String): Future[Option[Any]] = {
      repository.getSensorValue(deviceId,sensorName)
    }
    def getSensorsValues(deviceId:String): Future[Map[String,Any]] = {
      repository.getSensorsValues(deviceId)
    }
  }
}

object SensorsServiceComponent{
  type dependencies = SensorsRepositoryComponent
}

