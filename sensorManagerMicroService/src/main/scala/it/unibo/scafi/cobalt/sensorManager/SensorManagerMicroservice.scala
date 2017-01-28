package it.unibo.scafi.cobalt.sensorManager

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.sensorManager.core.SensorManagerServiceComponent
import it.unibo.scafi.cobalt.sensorManager.impl.{AkkaHttpSensorManagerRoutingComponent, RedisSensorManagerRepoComponent}
import redis.RedisClient

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */
object SensorManagerMicroservice extends App with Config{
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val router = new AkkaHttpSensorManagerRoutingComponent with SensorManagerServiceComponent with RedisSensorManagerRepoComponent {
    override val redisClient: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))
  }

  Http().bindAndHandle(router.routes, interface, port)
}