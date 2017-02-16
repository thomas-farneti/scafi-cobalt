package it.unibo.scafi.cobalt.test.executionService

import akka.actor.ActorSystem
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Await
import scala.util.{Failure, Success}

/**
  * Created by tfarneti.
  */
class ScafiExecutionServiceSpec extends WordSpec with Matchers {
  implicit val system = ActorSystem()
  implicit val exec = system.dispatcher

  "The Execution service" should {


    val env = new TestEnvironment

    "Compute a new state for device" in {

      import scala.concurrent.duration._

      0 to 5 foreach{ i =>
        println(Await.result(env.service.execRound(""+((i % 3)+1)), 1 second).export)
      }

    }
  }
}
