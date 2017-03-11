package it.unibo.scafi.cobalt.executionService

import java.time.{Duration, LocalDateTime}
import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import io.prometheus.client.{Counter, Histogram}
import io.scalac.amqp._
import it.unibo.scafi.cobalt.common.infrastructure.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider, RabbitPublisher}
import it.unibo.scafi.cobalt.common.messages.{DeviceSensorsUpdated, FieldUpdated}
import it.unibo.scafi.cobalt.executionService.core.ScafiCobaltIncarnation
import it.unibo.scafi.cobalt.executionService.impl._
import it.unibo.scafi.cobalt.executionService.impl.scafi.{ScafiExecutionGatewayComponent, ScafiExecutionServiceComponent, ScafiRedisExecutionRepoComponent}
import redis.RedisClient

import scala.concurrent.ExecutionContext


trait Environment extends
  ScafiExecutionServiceComponent
  with ScafiRedisExecutionRepoComponent
  with ScafiExecutionGatewayComponent
  with DockerConfig
  with ServicesConfiguration
  with ActorSystemProvider
  with ActorMaterializerProvider
  with ExecutionContextProvider

object ExecutionService extends App with DockerConfig with AkkaHttpConfig with RedisConfiguration{
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

  //Http().bindAndHandle(env.executionRoutes, interface, port)

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
  val latency: Histogram = Histogram.build().name("execution_latency").help("Latency").register()

  pub.sourceFromRabbit[DeviceSensorsUpdated]("sensor_events.executionService.queue")
   .mapAsync(1)(data => {
    requestsServed.inc()
    env.service.execRound(data.deviceId).map(a => data -> a)
  })
  .withAttributes(supervisionStrategy(resumingDecider))
  .map(m => {
    val lat =  Duration.between(m._1.timestamp, LocalDateTime.now()).abs().getNano()
    latency.observe(lat)
    m
  })
  .map(s => FieldUpdated(UUID.randomUUID().toString,"FieldUpdated",LocalDateTime.now(),s._1.deviceId,s._1.lat,s._1.lon,s._2.root()))
  .alsoTo(Sink.foreach(_ => processed.inc()))
  .runWith(pub.sinkToRabbit("field_events", "FieldUpdated"))
}