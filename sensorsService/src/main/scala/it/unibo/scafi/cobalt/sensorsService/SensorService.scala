package it.unibo.scafi.cobalt.sensorsService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.sensorsService.core.SensorsServiceComponent
import it.unibo.scafi.cobalt.sensorsService.impl.{RedisSensorsRepositoryComponent, SensorsApiComponent}
import redis.RedisClient

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */
object SensorService extends App with DockerConfig with RedisConfiguration with AkkaHttpConfig{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val router = new SensorsApiComponent with SensorsServiceComponent with RedisSensorsRepositoryComponent {
    override val redisClient: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))
  }

  Http().bindAndHandle(router.routes, interface, port)
}
