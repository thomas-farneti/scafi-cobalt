package it.unibo.scafi.cobalt.test.executionService.scafi

import akka.actor.ActorSystem
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Await

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


      val res = for {
        r1 <- env.service.execRound("1")
        r2 <- env.service.execRound("2")
        r3 <- env.service.execRound("3")
        r4 <- env.service.execRound("1")
        r5 <- env.service.execRound("2")
        r6 <- env.service.execRound("3")
      }yield r6

//      0 to 8 foreach{ i =>
//        println(Await.result(env.service.execRound(""+((i % 3)+1)), 15 hour))
//        //env.service.execRound(""+((i % 3)+1)).map(e => println(e.export.root()))
//      }

      Await.result(res, 10 hours)
    }
  }
}
