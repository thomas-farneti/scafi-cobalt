package it.unibo.scafi.cobalt.services.networkService

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by tfarneti.
  */
trait Protocols extends SprayJsonSupport with DefaultJsonProtocol{

}
