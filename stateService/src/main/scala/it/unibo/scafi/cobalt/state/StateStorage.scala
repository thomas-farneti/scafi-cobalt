package it.unibo.scafi.cobalt.state

import it.unibo.scafi.incarnations.Incarnation

import scala.concurrent.Future

/**
  * Created by tfarneti on 04/01/2017.
  */
trait StateStorage { self: StateStorage.Dependency =>

  def store(key:ID)(state:EXPORT): Boolean
  def getAsync(key:ID): Future[Option[EXPORT]]
  def get(key:ID): Option[EXPORT]
}

object StateStorage {
  type Dependency = Incarnation
}