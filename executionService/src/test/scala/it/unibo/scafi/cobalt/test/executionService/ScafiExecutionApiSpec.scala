package it.unibo.scafi.cobalt.test.executionService

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import it.unibo.scafi.cobalt.executionService.impl.scafi._
import org.scalatest.{Matchers, WordSpec}



/**
  * Created by tfarneti.
  */
class ScafiExecutionApiSpec extends WordSpec with Matchers with ScalatestRouteTest{
  "The Execution service" should {

    val env = new TestEnvironment()

    val api = new ScafiExecutionServiceApiComponent(env)

    "Compute a new state for device" in {
      Post("/compute/1") ~> api.executionRoutes ~> check {
        status shouldEqual OK
        println(responseAs[String])
      }
      Post("/compute/2") ~> api.executionRoutes ~> check {
        status shouldEqual OK
        println(responseAs[String])
      }
      Post("/compute/3") ~> api.executionRoutes ~> check {
        status shouldEqual OK
        println(responseAs[String])
      }
    }
  }
}
