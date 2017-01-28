package it.unibo.scafi.cobalt.core.eventManager

import it.unibo.scafi.cobalt.core.messages.Event

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait EventPublisherComponent {
  def eventSender : EventSender
  trait EventSender{
    def publish(event: Event): Future[Boolean]
  }
}

trait EventConsumerComponent{
  type EventHandler[T] = Event => T

  trait EventConsumer{
    def subscribe[T](event: Class[Event], e: EventHandler[T])
  }
}
