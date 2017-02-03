package it.unibo.scafi.cobalt.ingestionService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import io.scalac.amqp.Connection
import it.unibo.scafi.cobalt.common.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceComponent
import it.unibo.scafi.cobalt.ingestionService.impl.{AkkaHttpIngestionRoutingComponent, RedisIngestionServiceRepoComponent}
import redis.RedisClient

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

/**
  * Created by tfarneti.
  */
object IngestionMicroService extends App with DockerConfig with RedisConfiguration with AkkaHttpConfig{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit def dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val redis : RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))

  val router = new AkkaHttpIngestionRoutingComponent(Connection(config)) with IngestionServiceComponent with RedisIngestionServiceRepoComponent with ActorSystemProvider with ExecutionContextProvider with ActorMaterializerProvider{
    override implicit val impSystem: ActorSystem = actorSystem
    override implicit def impExecutionContext: ExecutionContext = dispatcher
    override implicit val impmaterializer: ActorMaterializer = materializer

    override val redisClient: RedisClient = redis
  }

  Http().bindAndHandle(router.routes, interface, port)
}
