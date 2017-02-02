package it.unibo.scafi.cobalt.test.computingMicroService

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.{RouteTest, ScalatestRouteTest}
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.executionService.core.{CobaltBasicIncarnation, ExecutionGatewayMockComponent, ExecutionMockRepositoryComponent}
import it.unibo.scafi.cobalt.executionService.impl.{ActorSystemProvider, AkkaHttpExecutionComponent, CobaltExecutionServiceComponent}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContextExecutor

trait TestEnvironment extends AkkaHttpExecutionComponent
  with CobaltExecutionServiceComponent
  with ExecutionMockRepositoryComponent
  with ExecutionGatewayMockComponent
  with CobaltBasicIncarnation
  with ActorSystemProvider

/**
  * Created by tfarneti.
  */
class ComputingMicroServiceSpec extends WordSpec with Matchers with ScalatestRouteTest{
  "The Network service" should {

//    var env = new TestEnvironment {
////      override implicit def impSystem: ActorSystem = ActorSystem()
////      override implicit def impExecutor: ExecutionContextExecutor = impSystem.dispatcher
////      override implicit def impMat: ActorMaterializer = ActorMaterializer()
//      override implicit def impSystem: ActorSystem = ???
//      override implicit def impExecutor: ExecutionContextExecutor = ???
//      override implicit def impMat: ActorMaterializer = ???
//    }

    "Return a list of devices" in {
//      Post("/compute/1") ~> env.executionRoutes ~> check {
//        status shouldEqual OK
//        //responseAs[ComputeNewStateResponse].id shouldBe "1"
//      }
    }
  }

}
