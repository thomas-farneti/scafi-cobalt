import it.unibo.scafi.cobalt.core.messages.computingService.ComputingServiceMessages.ComputeNewState
import it.unibo.scafi.cobalt.core.services.computingService.{BasicComputingServiceComponent, ComputingMockGateway, MockComputingRepoCmp}
import it.unibo.scafi.incarnations.BasicAbstractIncarnation
import org.scalatest.{Matchers, WordSpec}


/**
  * Created by tfarneti.
  */
class ComputingServiceSpec extends WordSpec with Matchers with  BasicComputingServiceComponent with MockComputingRepoCmp  with BasicAbstractIncarnation with ComputingMockGateway {
  "The Computing Service" should {

    "Return a state" in {
      val res = service.computeNewState(ComputeNewState(1))
    }
  }
}
