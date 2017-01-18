package it.unibo.scafi.cobalt.platform

import it.unibo.scafi.platform.Platform.PlatformDependency

/**
  * Created by tfarneti on 05/01/2017.
  */
trait Platform extends it.unibo.scafi.distrib.actor.Platform { self: PlatformDependency =>

}

object Platform{
  type Subcomponent = Platform with PlatformDependency
}
