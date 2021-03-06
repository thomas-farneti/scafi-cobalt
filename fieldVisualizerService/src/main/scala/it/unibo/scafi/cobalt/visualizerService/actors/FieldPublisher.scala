/*
 * Copyright 2016 Achim Nierbeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unibo.scafi.cobalt.visualizerService.actors

import akka.actor.{ActorLogging, ActorRef, Props, Stash}
import akka.routing.{ActorRefRoutee, AddRoutee, RemoveRoutee}
import akka.stream.actor.ActorPublisher
import it.unibo.scafi.cobalt.common.domain.BoundingBox
import it.unibo.scafi.cobalt.common.messages.{FieldData, FieldUpdated}
import it.unibo.scafi.cobalt.common.messages.JsonProtocol._
import spray.json._

import scala.annotation.tailrec

object FieldPublisher {
  def props(router:ActorRef): Props =  Props(new FieldPublisher(router))
}

class FieldPublisher(router: ActorRef) extends ActorPublisher[String] with ActorLogging with Stash {

  case class QueueUpdated()

  import akka.stream.actor.ActorPublisherMessage._
  import context._

  import scala.collection.mutable

  val MaxBufferSize = 50
  val queue = mutable.Queue[FieldData]()

  var queueUpdated = false
  var tileIds: Set[String] = Set()

  // on startup, register with routee
  override def preStart() {
    log.info("Publisher preStart: adding self to routee")
    router ! AddRoutee(ActorRefRoutee(self))
  }

  // custom router only keeps track of alive actors
  override def postStop(): Unit = {
    log.info("Publisher postStop: removing self from routee")
    router ! RemoveRoutee(ActorRefRoutee(self))
  }

  def receive: Receive = {
    case bbox: BoundingBox => {
      log.info("received BBox changing behavior")
      //tileIds = TileCalc.convertBBoxToTileIDs(bbox)
      //log.info(s"${tileIds.size} tiles are requested")
      unstashAll()
      become(streamAndQueueVehicles, discardOld = false)
    }
    case msg => stash()
  }

  def streamAndQueueVehicles: Receive = {

    // receive new stats, add them to the queue, and quickly
    // exit.
    case data: FieldUpdated=>
      // remove the oldest one from the queue and add a new one
      log.info("RecivedData")
      if (queue.size == MaxBufferSize) queue.dequeue()
      //if (tileIds.contains(data.tileId)) {
        queue += FieldData(data.deviceId,data.lat,data.lon,data.value.toDouble)

        if (!queueUpdated) {
          queueUpdated = true
          self ! QueueUpdated
        }
      //}
    // we receive this message if there are new items in the
    // queue. If we have a demand for messages send the requested
    // demand.
    case QueueUpdated => deliver()

    // the connected subscriber request n messages, we don't need
    // to explicitely check the amount, we use totalDemand propery for this
    case Request(amount) =>
      deliver()

    // subscriber stops, so we stop ourselves.
    case Cancel =>
      context.stop(self)

    case stringMsg:String => {
      if ("close" == stringMsg) {
        log.info("closing websocket connection")
        become(receive, discardOld = true)
        router ! Cancel
      }
    }
  }

  /**
    * Deliver the message to the subscriber. In the case of websockets over TCP, note
    * that even if we have a slow consumer, we won't notice that immediately. First the
    * buffers will fill up before we get feedback.
    */
  @tailrec final def deliver(): Unit = {
    if (totalDemand == 0) {
      log.info(s"No more demand for: $this")
    }

    if (queue.size == 0 && totalDemand != 0) {
      // we can response to queueupdated msgs again, since
      // we can't do anything until our queue contains stuff again.
      queueUpdated = false
    } else if (totalDemand > 0 && queue.size > 0) {

      val fieldData = queue.dequeue()
      val fieldDataAsString = fieldData.toJson.compactPrint

      onNext(fieldDataAsString)
      deliver()
    }
  }
}
