package it.unibo.scafi.cobalt.computingService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingMockServiceGateway}
import it.unibo.scafi.cobalt.computingService.impl._
import redis.RedisClient

/**
  * Created by tfarneti.
  */

object ComputingMicroService extends App with Config{
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val env = new AkkaHttpRoutingComponent with CobaltComputingServiceComponent with RedisCobaltComputingRepoComponent with ComputingMockServiceGateway with CobaltBasicIncarnation {
    override val redisClient: RedisClient = RedisClient(host = redisHost, port = redisPort, password = Option(redisPassword), db = Option(redisDb))
  }

  Http().bindAndHandle(env.routes, config.getString("http.interface"), config.getInt("http.port"))
}
