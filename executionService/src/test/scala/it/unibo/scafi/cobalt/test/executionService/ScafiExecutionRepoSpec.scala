package it.unibo.scafi.cobalt.test.executionService

import it.unibo.scafi.cobalt.executionService.impl.scafi._
import org.scalatest.{Matchers, WordSpec}

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