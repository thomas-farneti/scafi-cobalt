package it.unibo.scafi.cobalt.executionService.impl

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import it.unibo.scafi.cobalt.common.{ActorMaterializerProvider, ActorSystemProvider, ExecutionContextProvider}
import it.unibo.scafi.cobalt.executionService.core.{CobaltBasicIncarnation, ExecutionServiceComponent}
/**
  * Created by tfarneti.
  */


trait AkkaHttpExecutionComponent { self: AkkaHttpExecutionComponent.dependencies =>

  val executionRoutes = {
    path("compute" / Segment){ deviceId =>
      post {
        extractRequestEntity{ entity =>
          entity.discardBytes()
          complete {
            service.computeNewState(deviceId).map[ToResponseMarshallable] { s =>
              s.id+" -> "+s.export
            }
          }
        }
      }
    }
  }
}


object AkkaHttpExecutionComponent{
  type dependencies = ExecutionServiceComponent with CobaltBasicIncarnation with ActorMaterializerProvider with ExecutionContextProvider
}
