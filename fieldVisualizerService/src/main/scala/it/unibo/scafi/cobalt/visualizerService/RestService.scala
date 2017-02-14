package it.unibo.scafi.cobalt.visualizerService

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait RestService extends CorsSupport {

  val logger: LoggingAdapter

  implicit val timeout = Timeout(3 seconds)

  def route()(implicit system: ActorSystem, ec: ExecutionContext): Route = {

    import akka.http.scaladsl.server.Directives._

//    val vehiclesPerBBox = system.actorOf(VehiclesPerBBoxActor.props(), "vehicles-per-bbox")
//    val routeDetailsPerId = system.actorOf(RouteDetailActor.props(), "route-details-id")
//    val routeInfosPerId = system.actorOf(RouteInfoActor.props(), "route-info-id")
//    val hotspots = system.actorOf(HotSpotsActor.props(), "hotspots")
//    val hotSpotDetailsPerId = system.actorOf(HotSpotDetailsActor.props(), "hotspotDetails")

    def service = pathSingleSlash {
      corsHandler {
        encodeResponse {
          // serve up static content from a JAR resource
          getFromResourceDirectory("")
        }
      }
    }

//    def vehiclesOnBBox = path("vehicles" / "boundingBox") {
//      corsHandler {
//        parameter('bbox.as[String], 'time.as[String] ? "5") { (bbox, time) =>
//          get {
//            marshal {
//              val boundingBox: BoundingBox = toBoundingBox(bbox)
//
//              val askedVehicles: Future[Future[List[Vehicle]]] = (vehiclesPerBBox ? (boundingBox, time)).mapTo[Future[List[Vehicle]]]
//              askedVehicles.flatMap(future => future)
//
//            }
//          }
//        }
//      }
//    }

//
//    val vehiclesPerBBoxService = Flow[Message].map {
//      case TextMessage.Strict(bbox) => {
//        val boundingBox: BoundingBox = toBoundingBox(bbox)
//
//        val vehicles = (vehiclesPerBBox ? boundingBox).mapTo[Future[List[Vehicle]]].flatMap(future => future)
//
//        JacksMapper.mapper.enable(SerializationFeature.INDENT_OUTPUT)
//        val result: Future[String] = vehicles.map(JacksMapper.writeValueAsString(_))
//        TextMessage(Source.fromFuture(result))
//      }
//      case _ => TextMessage("Message type unsupported")
//    }
//
//
//
//    // Websocket endpoints
//    def webSocketVehicles =
//      path("ws" / "vehicles" / "boundingBox") {
//        parameter('bbox.as[String]) { bbox =>
//          get {
//            logger.info("WebSocket request ...")
//            handleWebSocketMessages(vehiclesPerBBoxService)
//          }
//        }
//      }

      val vehiclesPerBBoxService = Flow[Message].map {
        case TextMessage.Strict(bbox) => {
          TextMessage("OK")
        }
        case _ => TextMessage("Message type unsupported")
      }

    // Websocket endpoints
    def webSocketVehicles =
      path("ws" / "devices" / "boundingBox") {
        parameter('bbox.as[String]) { bbox =>
          get {
            logger.info("WebSocket request ...")
            handleWebSocketMessages(vehiclesPerBBoxService)
          }
        }
      }

    // Frontend
    def index = (path("") | pathPrefix("index.htm")) {
      getFromResource("index.html")
    }
    def img = (pathPrefix("data") & path(Segment)) { resource => getFromResource(s"data/$resource") }

    def js = (pathPrefix("js") & path(Segment)) { resource => getFromResource(s"js/${resource}") }

    get {
      index ~ img ~ js
    } ~ service ~ webSocketVehicles//~ service ~ vehiclesOnBBox ~ routeInfo ~ routes ~ webSocketVehicles ~ hotSpots ~ hotSpotDetails
  }
//
//  def marshal(m: => Future[Any])(implicit ec: ExecutionContext): StandardRoute =
//    StandardRoute(ctx => {
//      ctx.complete({
//        JacksMapper.mapper.enable(SerializationFeature.INDENT_OUTPUT)
//        m.map(JacksMapper.writeValueAsString(_))
//      })
//    })

}
