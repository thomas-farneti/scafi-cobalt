package it.unibo.scafi.cobalt.domainService.test

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import it.unibo.scafi.cobalt.common.ExecutionContextProvider
import it.unibo.scafi.cobalt.domainService.core.{DomainRepositoryMockComponent, DomainServiceComponent}
import it.unibo.scafi.cobalt.domainService.impl.HttpDomainComponent
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext

class DomainServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with SprayJsonSupport{
  import spray.json.DefaultJsonProtocol._


  "The Domain service" should {
    val routing = new HttpDomainComponent with DomainServiceComponent with DomainRepositoryMockComponent with ExecutionContextProvider {
      override implicit val impExecutionContext: ExecutionContext = executor
    }

    "Return a list of devices" in {
      Get("/nbrs/1") ~> routing.routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Set[String]].head shouldBe "3"
      }
    }

    "Put a Device" in {
      Put("/nbrs/1/4") ~> routing.routes ~> check {
        status shouldEqual StatusCodes.Created
        //responseAs[Set[String]].head shouldBe "2"
      }
    }
  }
}