package it.unibo.scafi.cobalt.computingService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.computingService.core.CobaltBasicIncarnation
import it.unibo.scafi.cobalt.computingService.impl._
import it.unibo.scafi.cobalt.computingService.impl.gateway.DockerGatewayComponent
import it.unibo.scafi.cobalt.computingService.impl.repository.RedisCobaltComputingRepoComponent

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */

trait Environment extends AkkaComputingServiceComponent
  with CobaltComputingServiceComponent
  with RedisCobaltComputingRepoComponent
  with DockerGatewayComponent
  with CobaltBasicIncarnation
  with ActorSystemProvider
  with DockerConfig
  //with TestConfig
  with AkkaHttpConfig
  with RedisConfiguration
  with ServicesConfiguration


object ComputingMicroService extends App with Environment{

  override implicit def impSystem: ActorSystem = ActorSystem()

  override implicit def impExecutor: ExecutionContextExecutor = impSystem.dispatcher

  override implicit def impMat: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(computingServiceRoutes, interface, port)

}
