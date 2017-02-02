package it.unibo.scafi.cobalt.ingestionService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import io.scalac.amqp.Connection
import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceComponent
import it.unibo.scafi.cobalt.ingestionService.impl.{AkkaHttpIngestionRoutingComponent, RedisIngestionServiceRepoComponent}
import redis.RedisClient

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */
object IngestionMicroService extends App with DockerConfig with RedisConfiguration with AkkaHttpConfig{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val router = new AkkaHttpIngestionRoutingComponent(Connection(config)) with IngestionServiceComponent with RedisIngestionServiceRepoComponent {
    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()
    override val redisClient: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))
  }

  Http().bindAndHandle(router.routes, interface, port)
}
