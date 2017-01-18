package it.unibo.scafi.cobalt.core.messages

/**
  * Created by tfarneti on 18/01/2017.
  */
trait Messages {

  trait Message

  trait Command extends Message

  trait Query extends Message

  trait Event extends Message


  trait QueryHandler[Request<: Query,Response]{
    def query(req:Request): Response
  }

  trait CommandHandler[C <: Command]{
    def handle(cmd:C)
  }

}
