package it.unibo.scafi.cobalt.visualizerService

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.ws.{Message, TextMessage, UpgradeToWebSocket}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Source}
import akka.stream.{ActorMaterializer, FlowShape}
import it.unibo.scafi.cobalt.common.domain.{BoundingBox, LatLon}
import it.unibo.scafi.cobalt.visualizerService.actors.{FieldDatasFromRabbitActor, FieldPublisher, RouterActor}




object VisualizerService extends App with RestService with DockerConfig with AkkaHttpConfig{
  implicit val system = ActorSystem("service-api-http")
  implicit val mat = ActorMaterializer()
  implicit def ec = system.dispatcher

  val logger = Logging(system, getClass.getName)

  val router: ActorRef = system.actorOf(Props[RouterActor], "router")
  val vmactor: ActorRef = system.actorOf(FieldDatasFromRabbitActor.props(router), "Rabbit-Consumer")

  val requestHandler: HttpRequest => HttpResponse = {
    case req@HttpRequest(GET, Uri.Path("/ws/devices"), _, _, _) =>
      req.header[UpgradeToWebSocket] match {
        case Some(upgrade) => upgrade.handleMessages(graphFlowWithStats(router))
        case None => HttpResponse(400, entity = "Not a valid websocket request!")
      }
    case _: HttpRequest => HttpResponse(404, entity = "Unknown resource!")
  }

  Http().bindAndHandle(route(), interface, port)
  Http().bindAndHandleSync(requestHandler, interface, 8001)

  def graphFlowWithStats(router: ActorRef): Flow[Message, Message, _] = {
    Flow.fromGraph(GraphDSL.create() { implicit builder =>


      // create an actor source
      val source = Source.actorPublisher[String](FieldPublisher.props(router))

      // Graph elements we'll use
      val merge = builder.add(Merge[String](2))
      val filter = builder.add(Flow[String].filter(_ => false))

      // get BBox from request and send it to route, return nothing ...
      val mapMsgToString = builder.add(Flow[Message].map[String] {
        case TextMessage.Strict(msg) => {
          println(s"received message: $msg")
          if (msg.contains("close")) {
            router ! msg
          } else {
            val bbox = toBoundingBox(msg)
            println(s"transformedt to bbox: $bbox")
            router ! bbox
          }
          ""
        }
      })
      //outgoing message ...
      val mapStringToMsg = builder.add(Flow[String].map[Message](x => TextMessage.Strict(x)))

      //add source to flow
      val vehiclesSource = builder.add(source)

      // connect the graph
      mapMsgToString ~> filter ~> merge // this part of the merge will never provide msgs
      vehiclesSource ~> merge ~> mapStringToMsg

      // expose ports
      FlowShape(mapMsgToString.in, mapStringToMsg.out)
    })
  }

  def toBoundingBox(bbox: String): BoundingBox = {
    val bboxCoords: Array[String] = bbox.split(",")
    val boundingBox: BoundingBox =
      new BoundingBox(LatLon(bboxCoords(0).toFloat, bboxCoords(1).toFloat), LatLon(bboxCoords(2).toFloat, bboxCoords(3).toFloat))
    boundingBox
  }
}
