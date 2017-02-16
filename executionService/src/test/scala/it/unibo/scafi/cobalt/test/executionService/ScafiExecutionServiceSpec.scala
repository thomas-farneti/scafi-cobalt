package it.unibo.scafi.cobalt.test.executionService

import akka.actor.ActorSystem
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by tfarneti.
  */
class ScafiExecutionServiceSpec extends WordSpec with Matchers{
  implicit val system = ActorSystem()
  implicit val exec = system.dispatcher

  "The Execution service" should {


    val env = new TestEnvironment

    "Compute a new state for device" in {
      var res = for{
        s1 <- env.service.execRound("1")
        s2 <- env.service.execRound("2")
        s3 <- env.service.execRound("3")
      }yield ""+s1+"\n"+s2+"\n"+s3

      while (!res.isCompleted){}

      res.map(println(_))

      res = for{
        s1 <- env.service.execRound("1")
        s2 <- env.service.execRound("2")
        s3 <- env.service.execRound("3")
      }yield ""+s1+"\n"+s2+"\n"+s3

      while (!res.isCompleted){}

      res.map(println(_))
    }
  }
}
