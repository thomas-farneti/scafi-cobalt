package it.unibo.scafi.cobalt.ingestionService.core

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait IngestionServiceRepositoryComponent{
  def repository : Repository

  trait Repository{
    def setSensorValue(deviceId: String, sensorName:String, sensorValue:String) : Future[Boolean]
    def setSensorsValues(deviceId: String, sensorsValues: Map[String,String]) : Future[Boolean]
  }
}

trait IngestionServiceMockRepoComponent extends IngestionServiceRepositoryComponent{
  override def repository = new MockRepository

  class MockRepository() extends Repository{
    val db = Map("1" -> Map("s1" -> 1 , "s2" -> 2))

    override def setSensorValue(deviceId: String, sensorName: String, sensorValue: String): Future[Boolean] = {
      Future.successful(true)
    }

    override def setSensorsValues(deviceId: String, sensorsValues: Map[String, String]): Future[Boolean] = {
      Future.successful(true)
    }
  }
}