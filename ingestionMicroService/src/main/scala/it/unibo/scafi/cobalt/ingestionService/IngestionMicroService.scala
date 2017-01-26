package it.unibo.scafi.cobalt.ingestionService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceComponent
import it.unibo.scafi.cobalt.ingestionService.impl.{AkkaHttpIngestionRoutingComponent, RedisIngestionServiceRepoComponent}
import redis.RedisClient

import scala.concurrent.{ExecutionContextExecutor}

/**
  * Created by tfarneti.
  */
object IngestionMicroService extends App with Config{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val router = new AkkaHttpIngestionRoutingComponent with IngestionServiceComponent with RedisIngestionServiceRepoComponent {
    override val redisClient: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))
  }

  Http().bindAndHandle(router.routes, interface, port)
}
