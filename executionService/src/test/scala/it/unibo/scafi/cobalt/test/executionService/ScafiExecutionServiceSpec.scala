package it.unibo.scafi.cobalt.test.executionService

import akka.actor.ActorSystem
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.{Await, Future}
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

//      Future{
//        0 to 500 foreach{ _ =>
//          Await.result(env.service.execRound("1"), 1 second)
//        }
//      }
//
//      Future{
//        0 to 500 foreach{ _ =>
//          Await.result(env.service.execRound("2"), 1 second)
//        }
//      }
//
//      val dev3 = Future{
//        0 to 500 foreach{ _ =>
//          val res = Await.result(env.service.execRound("3"), 1 second)
//
//          println(res.export.root())
//        }
//      }

      0 to 8 foreach{ i =>
        println(Await.result(env.service.execRound(""+((i % 3)+1)), 15 hour))
        //env.service.execRound(""+((i % 3)+1)).map(e => println(e.export.root()))
      }

      //Await.result(dev3, 10 second)
    }
  }
}
