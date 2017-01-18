package it.unibo.scafi.cobalt.core.devices

import it.unibo.scafi.cobalt.core.messages.Messages

/**
  * Created by tfarneti on 18/01/2017.
  */
trait DevicesComponent { self: Messages =>
  type DeviceState
  type DeviceSensor

  class Device (id:String) {
    val state : DeviceState = ???
    val sensors : Map[String, DeviceSensor] = Map.empty[String,DeviceSensor]
  }

  class DevicesService{

  }

  case class GetDevice(id:String) extends Query
  case class DeviceResult(id:String,state:DeviceState, sensors: Map[String, DeviceSensor])

  class DevicesReadModel{
    def query (q: GetDevice): Set[DeviceResult] = {
      Set.empty
    }
  }

}

