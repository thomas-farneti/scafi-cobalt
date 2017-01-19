package it.unibo.scafi.cobalt.services.network

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

/**
  * Created by tfarneti.
  */
object NetworkMicroService extends App with Service{
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)


  Http().bindAndHandle(null, config.getString("http.interface"), config.getInt("http.port"))
}
