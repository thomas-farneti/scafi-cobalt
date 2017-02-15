package it.unibo.scafi.cobalt.domainService

import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.Logging.InfoLevel
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.logRequestResult
import akka.stream.{ActorMaterializer, Attributes}
import io.scalac.amqp.{Connection, Queue}
import it.unibo.scafi.cobalt.common.infrastructure.{ExecutionContextProvider, RabbitPublisher}
import it.unibo.scafi.cobalt.common.messages.SensorUpdated
import it.unibo.scafi.cobalt.domainService.core.DomainServiceComponent
import it.unibo.scafi.cobalt.domainService.impl.{DomainApiComponent, RedisDomainRepositoryComponent}
import redis.RedisClient

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

/**
  * Created by tfarneti.
  */
object DomainService extends App with TestConfig with AkkaHttpConfig with RedisConfiguration{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val redis: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))

  val env = new DomainApiComponent with DomainServiceComponent with RedisDomainRepositoryComponent with ExecutionContextProvider{
    override val redisClient: RedisClient = redis
    override implicit val impExecutionContext: ExecutionContext = dispatcher
  }

  val routes = logRequestResult("domainService", InfoLevel)(env.routes)

  Http().bindAndHandle(routes, interface , port)

  val connection = Connection(config)
  connection.queueDeclare(Queue("sensor_events.domainMicroservice.queue",durable = true)).onComplete(_=>
  connection.queueBind("sensor_events.domainMicroservice.queue","sensor_events","*.gps"))

  val pub = new RabbitPublisher(connection)

  pub.sourceFromRabbit[SensorUpdated]("sensor_events.domainMicroservice.queue")
  .log("before-publish")
  .withAttributes(Attributes.logLevels(onElement = Logging.DebugLevel))
  .map(m => m.deviceId -> m.sensorValue.split(":"))
  .runForeach(m => env.service.updatePosition(m._1,m._2(0),m._2(1)))

//  Source.fromPublisher(connection.consume(queue = "sensor_events.domainMicroservice.queue"))
//    .map(m => ByteString.fromArray(m.message.body.toArray).utf8String.parseJson.convertTo[SensorData])
//    .map(m => m.deviceId -> m.sensorValue.split(":"))
//    .alsoTo(Sink.foreach(m => println(m._1)))
//    .runForeach(m => env.service.updatePosition(m._1,m._2(0),m._2(1)))
}