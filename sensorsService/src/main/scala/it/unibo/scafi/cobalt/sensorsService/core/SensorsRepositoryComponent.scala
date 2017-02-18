package it.unibo.scafi.cobalt.sensorsService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait SensorsRepositoryComponent{
  def repository : Repository

  trait Repository{
    def getSensorValue(deviceId: String, sensorName:String) : Future[Option[Any]]
    def getSensorsValues(deviceId: String): Future[Map[String,Any]]
  }
}

