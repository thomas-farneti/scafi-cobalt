package it.unibo.scafi.cobalt.services.network

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

/**
  * Created by tfarneti.
  */
object NetworkMicroService extends App{
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

//  val config = ConfigFactory.load()
//  val logger = Logging(system, getClass)


  //Http().bindAndHandle(null, config.getString("http.interface"), config.getInt("http.port"))
}
