package it.unibo.scafi.cobalt.core.network

import it.unibo.scafi.cobalt.core.messages.Messages


import scala.collection.mutable.{Map => MMap}

/**
  * Created by tfarneti on 12/01/2017.
  */
trait NetworkComponent extends NetworkReadModel{ self: NetworkComponent.dependencies =>
  class NetworkService{

  }

}

object  NetworkComponent{
  type dependencies = Messages
}
