package it.unibo.scafi.cobalt.ingestionService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.kafka.ProducerSettings
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.ingestionService.core.IngestionServiceComponent
import it.unibo.scafi.cobalt.ingestionService.impl.{AkkaHttpIngestionRoutingComponent, RedisIngestionServiceRepoComponent}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import redis.RedisClient

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */
object IngestionMicroService extends App with Config{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val producerSettings = ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer).withBootstrapServers("localhost:32768")//(s"$kafkaHost:$kafkaPort")

  val router = new AkkaHttpIngestionRoutingComponent(producerSettings) with IngestionServiceComponent with RedisIngestionServiceRepoComponent {
    override val redisClient: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))
  }

  Http().bindAndHandle(router.routes, interface, port)
}
