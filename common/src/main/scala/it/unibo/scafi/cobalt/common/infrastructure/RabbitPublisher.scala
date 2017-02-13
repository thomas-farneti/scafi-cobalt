package it.unibo.scafi.cobalt.common.infrastructure

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Sink, Source}
import io.scalac.amqp.{Connection, Message, Routed}
import it.unibo.scafi.cobalt.common.messages.Event

/**
  * Created by tfarneti.
  */
class RabbitPublisher(connection : Connection){

  def publish[T <: Event](data : T, exchange: String,messageType:String): Unit ={
    Source.single(data).via(rabbitMapping(messageType)).to(Sink.fromSubscriber(connection.publish(exchange = exchange)))
  }

  def sinkToRabbit(exchange: String, messageType:String): Sink[Event, NotUsed] =
    rabbitMapping(messageType).to(Sink.fromSubscriber(connection.publish(exchange = exchange)))

  def sourceFromRabbit[T <: Event](queue:String): Source[T, NotUsed] = Source.fromPublisher(connection.consume(queue = queue))
    .map(m => deserialise[T](m.message.body.toArray))

  private def rabbitMapping(messageType:String): Flow[Event, Routed, NotUsed] = Flow[Event].map(data => Routed( s"${data.topic}", Message(`type`=Some(messageType), body = serialise(data))))

  private def serialise[T <: Event](value: T): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close
    stream.toByteArray
  }

  private def deserialise[T <: Event](bytes: Array[Byte]): T = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value = ois.readObject.asInstanceOf[T]
    ois.close
    value
  }
}
