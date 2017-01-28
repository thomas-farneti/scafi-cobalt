package it.unibo.scafi.cobalt.core.messages

import spray.json._

/**
  * Created by tfarneti.
  */
case class SensorData(id: String, deviceId:String, sensorName:String, sensorValue:String)

object JsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat4(SensorData)
}