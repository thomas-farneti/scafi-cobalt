package it.unibo.scafi.cobalt.services.network

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by tfarneti.
  */
trait Protocols extends SprayJsonSupport with DefaultJsonProtocol{

}
