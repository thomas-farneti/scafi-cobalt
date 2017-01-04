package it.unibi.scafi.cobalt.state.redisImpl

/**
  * Created by tfarneti on 04/01/2017.
  */
object TestApp extends App{
  override def main(args: Array[String]): Unit = {
    val db = new RedisStateDatabase {}

    db.store("testkey")(new TestType("test"))

    val a = db.get("testkey")

    println(a)
  }
}
