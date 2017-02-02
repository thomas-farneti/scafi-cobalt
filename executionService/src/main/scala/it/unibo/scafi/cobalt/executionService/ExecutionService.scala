package it.unibo.scafi.cobalt.executionService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import io.scalac.amqp.{Connection, Queue}
import it.unibo.scafi.cobalt.core.messages.JsonProtocol._
import it.unibo.scafi.cobalt.core.messages.SensorData
import it.unibo.scafi.cobalt.executionService.core.CobaltBasicIncarnation
import it.unibo.scafi.cobalt.executionService.impl._
import it.unibo.scafi.cobalt.executionService.impl.gateway.DockerGatewayComponent
import it.unibo.scafi.cobalt.executionService.impl.repository.RedisExecutionRepositoryComponent
import redis.RedisClient
import spray.json._


trait Environment extends AkkaHttpExecutionComponent
  with CobaltExecutionServiceComponent
  with RedisExecutionRepositoryComponent
  with DockerGatewayComponent
  with CobaltBasicIncarnation
  with DockerConfig
  with ServicesConfiguration
  with ActorSystemProvider


object ExecutionService extends App with DockerConfig with AkkaHttpConfig with RedisConfiguration{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val redis: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))

  val env = new Environment {
    override implicit val impSystem = system
    override implicit val impExecutor = executionContext
    override implicit val impMat = materializer

    override val redisClient: RedisClient = redis
  }

  Http().bindAndHandle(env.executionRoutes, interface, port)

  val connection = Connection(config)
  connection.queueDeclare(Queue("sensor_events.executionService.queue",durable = true)).onComplete(_=>
  connection.queueBind("sensor_events.executionService.queue","sensor_events","*.gps"))

  Source.fromPublisher(connection.consume(queue = "sensor_events.executionService.queue"))
    .map(m => ByteString.fromArray(m.message.body.toArray).utf8String.parseJson.convertTo[SensorData])
    .mapAsync(4){ m=>
      println(s" Computing ${m.deviceId}")
      env.service.computeNewState(m.deviceId)
    }
    .runWith(Sink.ignore)
}

