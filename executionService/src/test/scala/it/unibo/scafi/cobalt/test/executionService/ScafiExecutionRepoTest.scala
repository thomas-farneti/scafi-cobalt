package it.unibo.scafi.cobalt.test.executionService

import akka.actor.ActorSystem
import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.executionService.impl.scafi.{ScafiIncarnation, ScafiMockExecutionRepositoryComponent}
import org.scalatest.{FlatSpec, Matchers, WordSpec}

import scala.concurrent.{Await, ExecutionContext, Future}

import scala.concurrent.ExecutionContext.Implicits.global

class TestRepoEvironment(@transient implicit val impExecutionContext: ExecutionContext) extends ScafiMockExecutionRepositoryComponent
with ScafiIncarnation
with ExecutionContextProvider
with Serializable

/**
  * Created by tfarneti.
  */
class ScafiExecutionRepoTest extends WordSpec with Matchers {
//  implicit val system = ActorSystem()
//  implicit val exec = system.dispatcher

//  "The Repository" should {
//
//    val env = new TestRepoEvironment()
//
//    "Serialize a new state for device" in {
//
//      val exp = env.StateImpl("1",env.factory.emptyExport())
//
//      val resf = for{
//        set <-  env.repository.set("1", exp)
//        get <- env.repository.get("1")
//      }yield get
//
//
//      import scala.concurrent.duration._
//      val res = Await.result(resf, 1 second)
//
//      res.get.export == exp.export shouldEqual true
//
//
//    }
//  }
}
