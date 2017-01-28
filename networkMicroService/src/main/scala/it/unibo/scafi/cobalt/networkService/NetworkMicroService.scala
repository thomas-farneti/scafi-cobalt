package it.unibo.scafi.cobalt.networkService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.networkService.core.NetworkServiceComponent
import it.unibo.scafi.cobalt.networkService.impl.{AkkaHttpNetworkRoutingComponent, RedisNetworkServiceRepoComponent}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
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

  val consumerSettings = ConsumerSettings(actorSystem, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:32768")
    .withGroupId("networkService")
    //    .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    //    .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    .withProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,"30000")

  val source = Consumer.committableSource(consumerSettings, Subscriptions.topics("sensorData"))
  source.map(message => message.record.value()).runForeach(s => println(s))

  Http().bindAndHandle(router.routes, interface, port)

}
