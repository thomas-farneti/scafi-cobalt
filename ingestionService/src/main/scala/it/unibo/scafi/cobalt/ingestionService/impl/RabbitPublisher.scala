package it.unibo.scafi.cobalt.ingestionService.impl

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Sink, Source}
import io.scalac.amqp.{Connection, Message, Routed}
import it.unibo.scafi.cobalt.common.messages.Event

/**
  * Created by tfarneti.
  */
class RabbitPublisher(connection : Connection){

  def rabbitMapping[T](routingKey:String,messageType:String): Flow[T, Routed, NotUsed] = Flow[T].map(data => Routed( s"$routingKey", Message(`type`=Some(messageType), body = serialise(data))))

  def publish[T](data : T, routingKey: String, exchange: String,messageType:String): Unit ={
    Source.single(data).via(rabbitMapping(routingKey,messageType)).to(Sink.fromSubscriber(connection.publish(exchange = exchange)))
  }

  def streamToPublisher[T](routingKey:String, exchange: String,messageType:String): Sink[T, NotUsed] =
    rabbitMapping(routingKey,messageType)
      .to(Sink.fromSubscriber(connection.publish(exchange = exchange)))

  def streamToPublisher(exchange: String,messageType:String): Sink[Event, NotUsed] =
    mapping(messageType)
      .to(Sink.fromSubscriber(connection.publish(exchange = exchange)))

  def mapping(messageType:String): Flow[Event, Routed, NotUsed] = Flow[Event].map(data => Routed( s"${data.topic}", Message(`type`=Some(messageType), body = serialise(data))))

  def serialise(value: Any): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close
    stream.toByteArray
  }

  def deserialise[T](bytes: Array[Byte]): T = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value = ois.readObject.asInstanceOf[T]
    ois.close
    value
  }
}
