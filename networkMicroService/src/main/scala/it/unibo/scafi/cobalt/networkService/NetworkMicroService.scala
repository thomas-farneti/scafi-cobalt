package it.unibo.scafi.cobalt.networkService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.networkService.core.NetworkServiceComponent
import it.unibo.scafi.cobalt.networkService.impl.{AkkaHttpNetworkRoutingComponent, RedisNetworkServiceRepoComponent}
import redis.RedisClient

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

/**
  * Created by tfarneti.
  */
object NetworkMicroService extends App with Config{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val router = new AkkaHttpNetworkRoutingComponent with NetworkServiceComponent with RedisNetworkServiceRepoComponent {
    override val redisClient: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))
    override implicit val ec: ExecutionContext = dispatcher
  }

  Http().bindAndHandle(router.routes, interface, port)
}
