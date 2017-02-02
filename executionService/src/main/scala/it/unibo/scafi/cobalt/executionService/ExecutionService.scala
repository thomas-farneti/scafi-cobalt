package it.unibo.scafi.cobalt.executionService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.executionService.core.CobaltBasicIncarnation
import it.unibo.scafi.cobalt.executionService.impl._
import it.unibo.scafi.cobalt.executionService.impl.gateway.DockerGatewayComponent
import it.unibo.scafi.cobalt.executionService.impl.repository.RedisExecutionRepositoryComponent

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by tfarneti.
  */

trait Environment extends AkkaHttpExecutionComponent
  with CobaltExecutionServiceComponent
  with RedisExecutionRepositoryComponent
  with DockerGatewayComponent
  with CobaltBasicIncarnation
  with ActorSystemProvider
  with DockerConfig
  //with TestConfig
  with AkkaHttpConfig
  with RedisConfiguration
  with ServicesConfiguration


object ExecutionService extends App with Environment{

  override implicit def impSystem: ActorSystem = ActorSystem()

  override implicit def impExecutor: ExecutionContextExecutor = impSystem.dispatcher

  override implicit def impMat: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(computingServiceRoutes, interface, port)

}
