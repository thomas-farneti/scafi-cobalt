package it.unibo.scafi.cobalt.ingestion.test

/**
  * Created by tfarneti.
  */
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import it.unibo.scafi.cobalt.ingestionService.core.{IngestionServiceComponent, IngestionServiceMockRepoComponent}
import it.unibo.scafi.cobalt.ingestionService.impl.AkkaHttpIngestionRoutingComponent
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by tfarneti.
  */
class IngestionServiceSpec extends WordSpec with Matchers with ScalatestRouteTest{
//  "The Network service" should {
//    val env = new AkkaHttpIngestionRoutingComponent with  IngestionServiceComponent with IngestionServiceMockRepoComponent
//
//    "Return a list of devices" in {
//      Put("/device/1/sensor/s1/1") ~> env.routes ~> check {
//        status shouldEqual OK
//        //responseAs[ComputeNewStateResponse].id shouldBe "1"
//      }
//    }
//  }

}
