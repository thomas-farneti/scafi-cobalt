package it.unibo.scafi.cobalt.common.messages

import java.time.{LocalDate, LocalDateTime}

/**
  * Created by tfarneti.
  */
trait Event {
  val id:String
  val topic: String
  val timestamp : LocalDateTime
}

case class DeviceSensorsUpdated(id:String, topic:String,timestamp : LocalDateTime, deviceId:String, lat:Double, lon:Double, sensorsData: Map[String,String]) extends Event

case class FieldUpdated(id:String, topic:String,timestamp : LocalDateTime, deviceId:String,lat:Double,lon:Double, value:String) extends Event