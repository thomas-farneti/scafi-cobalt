package it.unibo.scafi.cobalt.test.computingMicroService

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import it.unibo.scafi.cobalt.computingService.core.{ComputingMockGateway, ComputingRepositoryMockComponent, ComputingServiceComponent}
import it.unibo.scafi.cobalt.computingService.impl.{AkkaHttpRoutingComponent, Protocols}
import it.unibo.scafi.cobalt.core.incarnation.BasicCobaltIncarnation
import it.unibo.scafi.cobalt.core.messages.computingService.{ComputeNewStateCommand, ComputeNewStateResponse}
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by tfarneti.
  */
class ComputingMicroServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with Protocols{
  "The Network service" should {
    val env = new AkkaHttpRoutingComponent with Protocols with ComputingServiceComponent with ComputingRepositoryMockComponent with ComputingMockGateway with BasicCobaltIncarnation

    "Return a list of devices" in {
      Post("/compute", ComputeNewStateCommand("1")) ~> env.routes ~> check {
        status shouldEqual OK
        responseAs[ComputeNewStateResponse].id shouldBe "1"
      }
    }
  }

}
