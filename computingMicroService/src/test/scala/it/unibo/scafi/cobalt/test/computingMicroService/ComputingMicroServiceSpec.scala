package it.unibo.scafi.cobalt.test.computingMicroService

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import it.unibo.scafi.cobalt.computingService.core.{CobaltBasicIncarnation, ComputingMockServiceGateway, ComputingRepositoryMockComponentCobalt}
import it.unibo.scafi.cobalt.computingService.impl.{AkkaHttpRoutingComponent, CobaltComputingServiceComponent}
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by tfarneti.
  */
class ComputingMicroServiceSpec extends WordSpec with Matchers with ScalatestRouteTest{
  "The Network service" should {
    val env = new AkkaHttpRoutingComponent with CobaltComputingServiceComponent with ComputingRepositoryMockComponentCobalt with ComputingMockServiceGateway with CobaltBasicIncarnation

    "Return a list of devices" in {
      Post("/compute/1") ~> env.routes ~> check {
        status shouldEqual OK
        //responseAs[ComputeNewStateResponse].id shouldBe "1"
      }
    }
  }

}
