package it.unibo.scafi.cobalt.common.messages

/**
  * Created by tfarneti.
  */
trait Event {
  val id:String
  val topic: String
}

case class SensorUpdated(id:String, topic:String, deviceId:String, sensorName:String, sensorValue:String) extends Event

case class FieldUpdated(id:String, topic:String, deviceId:String, value:String) extends Event