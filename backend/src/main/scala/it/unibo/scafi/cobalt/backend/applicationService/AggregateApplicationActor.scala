package it.unibo.scafi.cobalt.backend.applicationService

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.persistence.PersistentActor

object AggregateApplicationActor{
  case class start()
  case class stop()

  def props(id: String): Props = Props(new AggregateApplicationActor(id))
}


/**
  * Created by tfarneti on 12/01/2017.
  */
class AggregateApplicationActor(id:String) extends PersistentActor with ActorLogging{


  val masterProxy = context.actorOf(
    ClusterSingletonProxy.props(
      settings = ClusterSingletonProxySettings(context.system).withRole("backend"),
      singletonManagerPath = "/user/master"
    ),
    name = "masterProxy")

  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = ???

//  override def persistenceId: String = Cluster(context.system).selfRoles.find(_.startsWith("backend-")) match {
//    case Some(role) ⇒ role + "-master"
//    case None       ⇒ "master"
//  }
  override def persistenceId: String = id
}
