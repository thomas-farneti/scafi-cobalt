package it.unibo.scafi.cobalt.networkService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import io.scalac.amqp.{Connection, Queue}
import it.unibo.scafi.cobalt.core.messages.JsonProtocol._
import it.unibo.scafi.cobalt.core.messages.SensorData
import it.unibo.scafi.cobalt.networkService.core.NetworkServiceComponent
import it.unibo.scafi.cobalt.networkService.impl.{AkkaHttpNetworkRoutingComponent, RedisNetworkServiceRepoComponent}
import redis.RedisClient
import spray.json._

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */
object NetworkMicroService extends App with Config{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val router = new AkkaHttpNetworkRoutingComponent with NetworkServiceComponent with RedisNetworkServiceRepoComponent {
    override val redisClient: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))
  }

  Http().bindAndHandle(router.routes, interface, port)

  val connection = Connection(config)
  connection.queueDeclare(Queue("sensor_events.networkMicroservice.queue",durable = true))
  connection.queueBind("sensor_events.networkMicroservice.queue","sensor_events","*.gps")

  Source.fromPublisher(connection.consume(queue = "sensor_events.networkMicroservice.queue"))
      .map(m => ByteString.fromArray(m.message.body.toArray).utf8String.parseJson.convertTo[SensorData])
      .mapAsync(4){ m=>
        val split = m.sensorValue.split(":")
        println(s"${m.deviceId} ${split(0)} ${split(1)}")
        router.service.updatePosition(m.deviceId,split(0),split(1))
      }
      .runWith(Sink.ignore)
}
