package it.unibo.scafi.cobalt.test.computingMicroService

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.{RouteTest, ScalatestRouteTest}
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingMockServiceGateway, ComputingRepositoryMockComponentCobalt}
import it.unibo.scafi.cobalt.computingService.impl.{ActorSystemProvider, AkkaComputingServiceComponent, CobaltComputingServiceComponent}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContextExecutor

trait TestEnvironment extends AkkaComputingServiceComponent
  with CobaltComputingServiceComponent
  with ComputingRepositoryMockComponentCobalt
  with ComputingMockServiceGateway
  with CobaltBasicIncarnation
  with ActorSystemProvider

/**
  * Created by tfarneti.
  */
class ComputingMicroServiceSpec extends WordSpec with Matchers with ScalatestRouteTest{
  "The Network service" should {

    var env = new TestEnvironment {
//      override implicit def impSystem: ActorSystem = ActorSystem()
//      override implicit def impExecutor: ExecutionContextExecutor = impSystem.dispatcher
//      override implicit def impMat: ActorMaterializer = ActorMaterializer()
      override implicit def impSystem: ActorSystem = ???
      override implicit def impExecutor: ExecutionContextExecutor = ???
      override implicit def impMat: ActorMaterializer = ???
    }

    "Return a list of devices" in {
      Post("/compute/1") ~> env.computingServiceRoutes ~> check {
        status shouldEqual OK
        //responseAs[ComputeNewStateResponse].id shouldBe "1"
      }
    }
  }

}
