package it.unibo.scafi.cobalt.common.messages

/**
  * Created by tfarneti.
  */
trait Event {
  val id:String
  val topic: String
}

case class DeviceSensorsUpdated(id:String, topic:String, deviceId:String, lat:Double, lon:Double, sensorsData: Map[String,String]) extends Event

case class FieldUpdated(id:String, topic:String, deviceId:String,lat:Double,lon:Double, value:String) extends Event