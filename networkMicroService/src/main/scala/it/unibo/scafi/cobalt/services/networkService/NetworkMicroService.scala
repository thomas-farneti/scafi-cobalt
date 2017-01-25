package it.unibo.scafi.cobalt.services.networkService

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

/**
  * Created by tfarneti.
  */
object NetworkMicroService extends App with Config{
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val svc = new Router(new NetworkService(new NetworkRepoRedisImpl()))

  Http().bindAndHandle(svc.routes, config.getString("http.interface"), config.getInt("http.port"))
}
