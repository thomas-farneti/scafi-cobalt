package it.unibo.scafi.cobalt.services.computingService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by tfarneti.
  */
trait Gateway{
  def getNbrs(deviceId:String): Future[Set[String]]
}

trait Repository{
  def loadExports(deviceIds:Set[String]): Future[List[Export]]
}


class Service(gateway: Gateway, repository: Repository)(implicit executionContext: ExecutionContext) {

  def computeState(cmd: ComputeNewState): Future[Export] = {

    val deviceExport = repository.loadExports(Set(cmd.deviceId)).map(_.head)

    val nbrExp = for{
      nbrsIds <- gateway.getNbrs(cmd.deviceId)
      nbrExports <- repository.loadExports(nbrsIds)
    } yield nbrExports


    nbrExp.map(_.length).map(Export(cmd.deviceId,_))
  }

}

