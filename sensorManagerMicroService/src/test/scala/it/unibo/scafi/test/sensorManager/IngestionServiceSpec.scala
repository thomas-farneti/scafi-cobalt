package it.unibo.scafi.test.sensorManager

/**
  * Created by tfarneti.
  */
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import it.unibo.scafi.cobalt.sensorManager.core.{SensorManagerMockRepoComponent, SensorManagerServiceComponent}
import it.unibo.scafi.cobalt.sensorManager.impl.AkkaHttpSensorManagerRoutingComponent
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by tfarneti.
  */
class IngestionServiceSpec extends WordSpec with Matchers with ScalatestRouteTest{
  "The Network service" should {
    val env = new AkkaHttpSensorManagerRoutingComponent with  SensorManagerServiceComponent with SensorManagerMockRepoComponent

    "Return a list of devices" in {
      Get("/device/1/sensor/s1") ~> env.routes ~> check {
        status shouldEqual OK
        responseAs[String] shouldBe "1"
      }
    }
  }

}
