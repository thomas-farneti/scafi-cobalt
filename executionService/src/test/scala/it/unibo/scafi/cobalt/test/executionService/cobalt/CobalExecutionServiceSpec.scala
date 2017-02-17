package it.unibo.scafi.cobalt.test.executionService.cobalt

import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.core.ExecutionGatewayComponent
import it.unibo.scafi.cobalt.executionService.impl.cobalt.{CobaltBasicIncarnation, CobaltExecutionServiceComponent, ExecutionGatewayMockComponent, ExecutionMockRepositoryComponent}
import it.unibo.scafi.cobalt.test.executionService.scafi.TestEnvironment
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.{Await, ExecutionContext}

import scala.concurrent.ExecutionContext.Implicits.global

class CobaltEnvironment(implicit val impExecutionContext: ExecutionContext) extends CobaltExecutionServiceComponent
with ExecutionMockRepositoryComponent
with ExecutionGatewayMockComponent
with CobaltBasicIncarnation
with ExecutionContextProvider

/**
  * Created by tfarneti.
  */
class CobalExecutionServiceSpec extends WordSpec with Matchers{

  "The Cobalt Incarnation Execution service" should {

    val env = new CobaltEnvironment()

    "Compute a new state for device" in {

      import scala.concurrent.duration._

      val res = for {
        _ <- env.service.execRound("1")
        _ <- env.service.execRound("2")
        _ <- env.service.execRound("3")
        _ <- env.service.execRound("1")
        _ <- env.service.execRound("2")
        _ <- env.service.execRound("3")
        _ <- env.service.execRound("1")
        _ <- env.service.execRound("2")
        r9 <- env.service.execRound("3")
      }yield r9

      Await.result(res, 2 seconds) shouldEqual "2"
    }
  }

}
