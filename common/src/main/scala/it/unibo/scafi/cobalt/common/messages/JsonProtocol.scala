package it.unibo.scafi.cobalt.common.messages

import spray.json._

/**
  * Created by tfarneti.
  */
case class DeviceData(messageId: String, deviceId:String,lat: Double, lon:Double, sensorsData: Map[String,String])

case class FieldData(id:String, latitude: Double, longitude: Double)


object JsonProtocol extends DefaultJsonProtocol {
  implicit val deviceDataFormat: RootJsonFormat[DeviceData] = jsonFormat5(DeviceData)
  implicit val fieldDataFormat: RootJsonFormat[FieldData] = jsonFormat3(FieldData)
}