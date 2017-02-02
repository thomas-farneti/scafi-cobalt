package it.unibo.scafi.cobalt.domainService

/**
  * Created by tfarneti.
  */
import com.typesafe.config.{Config, ConfigFactory}

trait Configuration {
  protected val config : Config
}

trait DockerConfig extends Configuration{
  override protected val config: Config = ConfigFactory.load()
}

trait TestConfig extends Configuration{
  override protected val config: Config = ConfigFactory.load("test.conf")
}

trait AkkaHttpConfig extends Configuration{
  protected val interface: String = config.getString("http.interface")
  protected val port: Int = config.getInt("http.port")
}

trait RedisConfiguration extends Configuration {
//  override protected val config: Config = ConfigFactory.load()
  protected val redisHost = config.getString("redis.host")
  protected val redisPort = config.getInt("redis.port")
  protected val redisPassword = config.getString("redis.password")
  protected val redisDb = config.getInt("redis.db")
}
