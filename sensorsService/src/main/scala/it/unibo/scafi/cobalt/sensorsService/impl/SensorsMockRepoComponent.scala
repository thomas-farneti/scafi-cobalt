package it.unibo.scafi.cobalt.sensorsService.impl

import it.unibo.scafi.cobalt.sensorsService.core.SensorsRepositoryComponent

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait SensorsMockRepoComponent extends SensorsRepositoryComponent{
  override def repository = new MockRepository

  class MockRepository() extends Repository{
    val db = Map("1" -> Map("s1" -> "1" , "s2" -> "2"))

    override def getSensorValue(deviceId: String, sensorName: String): Future[Option[String]] = Future.successful(Some(db(deviceId)(sensorName)))

    override def getSensorsValues(deviceId: String): Future[Map[String, Any]] = Future.successful(db.get(deviceId) match {
      case Some(v) => v
      case _ => Map()
    })
  }
}
