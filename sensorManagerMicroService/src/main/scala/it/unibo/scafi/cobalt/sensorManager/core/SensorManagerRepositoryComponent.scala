package it.unibo.scafi.cobalt.sensorManager.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait SensorManagerRepositoryComponent{
  def repository : Repository

  trait Repository{
    def getSensorValue(deviceId: String, sensorName:String) : Future[Option[String]]
    def getSensorsValues(deviceId: String): Future[Option[Map[String,String]]]
  }
}

trait SensorManagerMockRepoComponent extends SensorManagerRepositoryComponent{
  override def repository = new MockRepository

  class MockRepository() extends Repository{
    val db = Map("1" -> Map("s1" -> "1" , "s2" -> "2"))

    override def getSensorValue(deviceId: String, sensorName: String): Future[Option[String]] = Future.successful(Some(db(deviceId)(sensorName)))

    override def getSensorsValues(deviceId: String): Future[Option[Map[String, String]]] = Future.successful(db.get(deviceId))
  }
}