import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.StatusCodes
import it.unibo.scafi.cobalt.services.network.{NetworkRepository, NetworkService, Routing}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class ServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with SprayJsonSupport{
  import spray.json.DefaultJsonProtocol._

  "The Network service" should {
    var repository = new MockRepo()

    "Return a list of devices" in {
      Get("/neighbors/1a3t") ~> new Routing(new NetworkService(repository)).routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Set[String]].size shouldBe 3
      }
    }
  }
}

class MockRepo extends NetworkRepository{
  override def getNeighborsIdForDevice(deviceId: String): Future[Set[String]] = Future.successful(Set("1","2","3"))

  override def addNeighborForDevice(deviceId: String, nbrId: String): Future[Boolean] = Future.successful(true)

  override def removeNeighborForDevice(deviceId: String, nbrId: String): Future[Boolean] = Future.successful(true)
}
