package it.unibo.scafi.cobalt.domainService

/**
  * Created by tfarneti.
  */
import com.typesafe.config.ConfigFactory

trait Config {
  protected val config = ConfigFactory.load()
  protected val interface = config.getString("http.interface")
  protected val port = config.getInt("http.port")
  protected val redisHost = config.getString("redis.host")
  protected val redisPort = config.getInt("redis.port")
  protected val redisPassword = config.getString("redis.password")
  protected val redisDb = config.getInt("redis.db")
}
