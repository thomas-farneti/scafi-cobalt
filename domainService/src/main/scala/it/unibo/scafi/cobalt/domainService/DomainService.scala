package it.unibo.scafi.cobalt.domainService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import io.scalac.amqp.{Connection, Queue}
import it.unibo.scafi.cobalt.core.messages.JsonProtocol._
import it.unibo.scafi.cobalt.core.messages.SensorData
import it.unibo.scafi.cobalt.domainService.core.DomainServiceComponent
import it.unibo.scafi.cobalt.domainService.impl.{HttpDomainComponent, RedisDomainRepositoryComponent}
import redis.RedisClient
import spray.json._

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */
object DomainService extends App with DockerConfig with AkkaHttpConfig with RedisConfiguration{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val router = new HttpDomainComponent with DomainServiceComponent with RedisDomainRepositoryComponent {
    override val redisClient: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))
  }

  Http().bindAndHandle(router.routes, interface , port)

  val connection = Connection(config)
  connection.queueDeclare(Queue("sensor_events.domainMicroservice.queue",durable = true)).onComplete(_=>
  connection.queueBind("sensor_events.domainMicroservice.queue","sensor_events","*.gps"))

  Source.fromPublisher(connection.consume(queue = "sensor_events.domainMicroservice.queue"))
      .map(m => ByteString.fromArray(m.message.body.toArray).utf8String.parseJson.convertTo[SensorData])
      .mapAsync(4){ m=>
        val split = m.sensorValue.split(":")
        println(s"${m.deviceId} ${split(0)} ${split(1)}")
        router.service.updatePosition(m.deviceId,split(0),split(1))
      }
      .runWith(Sink.ignore)
}
