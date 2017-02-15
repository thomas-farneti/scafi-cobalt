package it.unibo.scafi.cobalt.test.executionService

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import it.unibo.scafi.cobalt.common.infrastructure.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.impl.scafi._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext

class RepositoryTestEnvironment extends ScafiMockExecutionRepositoryComponent
  with ScafiIncarnation
  with Serializable


/**
  * Created by tfarneti.
  */
class ScafiExecutionRepoSpec extends WordSpec with Matchers {
  "The Execution Repository" should {

    val env = new RepositoryTestEnvironment

    "Store new state for device" in {
      env.repository.get("1")
    }
  }
}