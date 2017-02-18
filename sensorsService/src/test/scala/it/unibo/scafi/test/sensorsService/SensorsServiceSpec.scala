package it.unibo.scafi.test.sensorsService

/**
  * Created by tfarneti.
  */

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import it.unibo.scafi.cobalt.sensorsService.core.SensorsServiceComponent
import it.unibo.scafi.cobalt.sensorsService.impl.{SensorsApiComponent, SensorsMockRepoComponent}
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.ContentTypes._

import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

/**
  * Created by tfarneti.
  */
class SensorsServiceSpec extends WordSpec with Matchers with ScalatestRouteTest {
  "The Sensors service" should {
    val env = new SensorsApiComponent with SensorsServiceComponent with SensorsMockRepoComponent

    "Return the value of a single sensor for a specific device" in {
      Get("/device/1/sensor/s1") ~> env.routes ~> check {
        status shouldEqual OK
        responseAs[String] shouldBe "1"
      }
    }

    "Return the value of all sensors of a specific device" in {
      Get("/device/1") ~> env.routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        responseAs[Map[String,String]].apply("s1") shouldBe "1"
      }
    }
  }
}
