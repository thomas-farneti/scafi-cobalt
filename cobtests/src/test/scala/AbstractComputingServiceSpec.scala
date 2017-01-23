import it.unibo.scafi.cobalt.core.messages.computingService.ComputingServiceMessages.ComputeNewState
import it.unibo.scafi.cobalt.core.services.computingService.{BasicCobaltIncarnation, BasicComputingServiceComponent, ComputingMockGateway, ComputingRepositoryMockComponent}
import it.unibo.scafi.incarnations.BasicAbstractIncarnation
import org.scalatest.{Matchers, WordSpec}


/**
  * Created by tfarneti.
  */
class AbstractComputingServiceSpec extends WordSpec with Matchers {
  "The Computing Service" should {
    val env = new BasicComputingServiceComponent with ComputingRepositoryMockComponent  with BasicCobaltIncarnation with ComputingMockGateway
    "Return a state" in {
      val res = env.service.computeNewState(ComputeNewState("1"))
    }
  }
}
