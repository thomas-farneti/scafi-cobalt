package it.unibo.scafi.cobalt.computingService.impl

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

trait ConsulConfiguration extends Configuration{
//  override protected val config: Config = ConfigFactory.load()
  protected val consulHost = config.getString("consul.host")
  protected val consulPort = config.getInt("consul.port")
}

trait ServicesConfiguration extends Configuration{
//  override protected val config: Config = ConfigFactory.load()
  protected val networkHost = config.getString("services.network.host")
  protected val networkPort = config.getInt("services.network.port")
  protected val sensorHost = config.getString("services.sensor.host")
  protected val sensorPort = config.getInt("services.sensor.port")
}