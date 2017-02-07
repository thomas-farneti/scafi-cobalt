package it.unibo.scafi.cobalt.common.messages

import spray.json._

/**
  * Created by tfarneti.
  */
case class SensorData(id: String, deviceId:String, sensorName:String, sensorValue:String) extends Event

case class FieldData(id:String, latitude: Double, longitude: Double) extends Event

object JsonProtocol extends DefaultJsonProtocol {
  implicit val sensorDataFormat = jsonFormat4(SensorData)
  implicit val fieldDataFormat = jsonFormat3(FieldData)
}