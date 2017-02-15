package it.unibo.scafi.cobalt.test.executionService

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.StatusCodes._
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.common.infrastructure.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.impl.scafi._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext

class TestEnvironment(@transient implicit val impExecutionContext: ExecutionContext ) extends
  ScafiExecutionServiceComponent
  with ScafiMockExecutionRepositoryComponent
  with ScafiMockExecutionGatewayComponent
  with ScafiIncarnation
  with ExecutionContextProvider

/**
  * Created by tfarneti.
  */
class ScafiExecutionServiceSpec extends WordSpec with Matchers with ScalatestRouteTest{
  "The Execution service" should {

    val env = new TestEnvironment()

    val api = new ScafiExecutionServiceApiComponent(env)

    "Compute a new state for device" in {
      Post("/compute/1") ~> api.executionRoutes ~> check {
        status shouldEqual OK
        println(responseAs[String])
      }

    }
  }
}
