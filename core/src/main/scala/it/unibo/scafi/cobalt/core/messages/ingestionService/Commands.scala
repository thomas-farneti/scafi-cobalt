package it.unibo.scafi.cobalt.core.messages.ingestionService

/**
  * Created by tfarneti.
  */
case class UpdateSensorValueCmd(deviceId: String, sensorName:String, sensorValue:String)

case class UpdateSensorsValues(deviceId:String, sensorsValues: Map[String,String])