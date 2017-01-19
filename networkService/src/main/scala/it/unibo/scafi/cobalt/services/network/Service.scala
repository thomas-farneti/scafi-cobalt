package it.unibo.scafi.cobalt.services.network


import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by tfarneti.
  */
class Service(repo:Repository) extends Protocols{

  def getNeighbors(deviceId: String) : Future[Either[String,Set[String]]] = {
    Future.successful(Right(Set.empty[String]))
  }
}
