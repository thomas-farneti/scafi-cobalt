package it.unibi.scafi.cobalt.state

import scala.concurrent.Future

/**
  * Created by tfarneti on 04/01/2017.
  */
trait StateDatabase {
  type KEY
  type STATE <: Export

  trait Export{

  }

  def store(key:KEY)(state:STATE): Boolean
  def getAsync(key:KEY): Future[Option[STATE]]
  def get(key:KEY): Option[STATE]

}
