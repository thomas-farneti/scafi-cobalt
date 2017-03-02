package it.unibo.scafi.cobalt.executionService

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import io.prometheus.client.Counter
import io.scalac.amqp._
import it.unibo.scafi.cobalt.common.infrastructure.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider, RabbitPublisher}
import it.unibo.scafi.cobalt.common.messages.{DeviceSensorsUpdated, FieldUpdated}
import it.unibo.scafi.cobalt.executionService.impl._
import it.unibo.scafi.cobalt.executionService.impl.cobalt._
import redis.RedisClient

import scala.concurrent.ExecutionContext


trait Environment extends ExecutionApiComponent
  with CobaltExecutionServiceComponent
  with CobaltRedisExecutionRepositoryComponent
  with CobaltExecutionGatewayComponent
  with CobaltBasicIncarnation
  with DockerConfig
  with ServicesConfiguration
  with ActorSystemProvider
  with ActorMaterializerProvider
  with ExecutionContextProvider


object ExecutionService extends App with DockerConfig with AkkaHttpConfig with RedisConfiguration with CobaltBasicIncarnation{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit def executionContext = system.dispatcher
  val redis: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))

  val env = new Environment {

    override val redisClient: RedisClient = redis
    override implicit val impmaterializer: ActorMaterializer = materializer
    override implicit def impExecutionContext: ExecutionContext = executionContext
    override implicit val impSystem: ActorSystem = system
  }

  Http().bindAndHandle(env.executionRoutes, interface, port)

  val requestsServed = Counter.build()
    .name("requests_served_total")
      .help("Total Request processed").register()

  val connection = Connection(config)
  connection.queueDeclare(Queue("sensor_events.executionService.queue",durable = true)).onComplete(_=>
  connection.queueBind("sensor_events.executionService.queue","sensor_events","*"))

  connection.exchangeDeclare(Exchange("field_events", Topic, durable = true))
  connection.queueDeclare(Queue("field_events.test.queue",durable = true)).onComplete(_=>
  connection.queueBind("field_events.test.queue","field_events","*"))

  import akka.stream.ActorAttributes.supervisionStrategy
  import akka.stream.Supervision.resumingDecider

  val pub = new RabbitPublisher(connection)
  val processed: Counter = Counter.build().name("execution_messageProcessed").help("Total requests.").register()

  pub.sourceFromRabbit[DeviceSensorsUpdated]("sensor_events.executionService.queue")
   .mapAsync(1)(data => {
    requestsServed.inc()
    env.service.execRound(data.deviceId).map(a => data -> a)
  })
  .withAttributes(supervisionStrategy(resumingDecider))
  .map(s => FieldUpdated(UUID.randomUUID().toString,"FieldUpdated",s._1.deviceId,s._1.lat,s._1.lon,s._2))
  .alsoTo(Sink.foreach(_ => processed.inc()))
  .runWith(pub.sinkToRabbit("field_events", "FieldUpdated"))
}