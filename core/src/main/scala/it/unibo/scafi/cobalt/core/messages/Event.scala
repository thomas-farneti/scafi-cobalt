package it.unibo.scafi.cobalt.core.messages

/**
  * Created by tfarneti.
  */
trait Event {
  val id:String
  val topic:String
}

case class SensorEvent(id:String, topic:String, deviceId:String, sensorName: String, sensorValue:String)