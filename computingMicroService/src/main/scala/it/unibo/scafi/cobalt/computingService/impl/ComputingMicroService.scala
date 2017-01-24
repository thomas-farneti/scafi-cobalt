package it.unibo.scafi.cobalt.computingService.impl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.computingService.ComputingServiceComponent
import it.unibo.scafi.cobalt.core.incarnation.BasicCobaltIncarnation
import redis.RedisClient

/**
  * Created by tfarneti.
  */

object ComputingMicroService extends App with Config{
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val env = new AkkaHttpRoutingComponent with Protocols with ComputingServiceComponent with RedisComputingRepositoryImpl with AkkaHttpGatewayComp with BasicCobaltIncarnation {
    override val redisClient: RedisClient = null
  }

  Http().bindAndHandle(env.routes, config.getString("http.interface"), config.getInt("http.port"))

}
