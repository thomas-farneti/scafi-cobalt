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

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.scalac.amqp._
import it.unibo.scafi.cobalt.core.messages.FieldData
import it.unibo.scafi.cobalt.core.messages.JsonProtocol._
import it.unibo.scafi.cobalt.visualizerService.DockerConfig
import spray.json._


object FieldDatasFromRabbitActor {

  def props(router:ActorRef):Props = Props(new FieldDatasFromRabbitActor(router))

}

class FieldDatasFromRabbitActor(router: ActorRef) extends Actor with ActorLogging with DockerConfig{
  implicit val materializer = ActorMaterializer()
  implicit val ec = context.system.dispatcher

  val connection = Connection(config)
  val qName = UUID.randomUUID().toString

  connection.queueDeclare(Queue(qName)).onComplete( _ =>
  connection.queueBind(qName,"field_events","*"))

  Source.fromPublisher(connection.consume(qName))
    .map(m => ByteString.fromArray(m.message.body.toArray).utf8String.parseJson.convertTo[FieldData])
      .runForeach(data => router ! data)



  override def receive: Actor.Receive = {
    case _ => // just ignore any messages
  }
}
