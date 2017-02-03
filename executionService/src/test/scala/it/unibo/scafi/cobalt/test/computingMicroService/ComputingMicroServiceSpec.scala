package it.unibo.scafi.cobalt.test.computingMicroService

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.common.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.core.{CobaltBasicIncarnation, ExecutionGatewayMockComponent, ExecutionMockRepositoryComponent}
import it.unibo.scafi.cobalt.executionService.impl.{AkkaHttpExecutionComponent, CobaltExecutionServiceComponent}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext

trait TestEnvironment extends AkkaHttpExecutionComponent
  with CobaltExecutionServiceComponent
  with ExecutionMockRepositoryComponent
  with ExecutionGatewayMockComponent
  with CobaltBasicIncarnation
  with ActorSystemProvider
  with ExecutionContextProvider
  with ActorMaterializerProvider

/**
  * Created by tfarneti.
  */
class ComputingMicroServiceSpec extends WordSpec with Matchers with ScalatestRouteTest{
  "The Network service" should {

    var env = new TestEnvironment {
      override implicit val impmaterializer: ActorMaterializer = materializer
      override implicit def impExecutionContext: ExecutionContext = executor
      override implicit val impSystem: ActorSystem = system
    }

    "Return a list of devices" in {
      Post("/compute/1") ~> env.executionRoutes ~> check {
        status shouldEqual OK
      }
    }
  }

}
