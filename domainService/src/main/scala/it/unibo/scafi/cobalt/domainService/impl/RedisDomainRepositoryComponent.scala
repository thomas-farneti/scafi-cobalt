package it.unibo.scafi.cobalt.domainService.impl

import it.unibo.scafi.cobalt.common.domain.{BoundingBox, LatLon}
import it.unibo.scafi.cobalt.common.infrastructure.ExecutionContextProvider
import it.unibo.scafi.cobalt.domainService.core.DomainRepositoryComponent
import redis.RedisClient
import redis.api.geo.DistUnits

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait RedisDomainRepositoryComponent extends DomainRepositoryComponent{ self : ExecutionContextProvider =>
  val redisClient : RedisClient

  override def repository = new RedisRepository(redisClient)

  class RedisRepository(redisClient:RedisClient) extends Repository {
    private def deviceIdToRedisKey(id: String): String = s"netSvc:id:$id"

    override def getNeighborsIdsForDevice(deviceId: String): Future[Set[String]] = redisClient.smembers[String](deviceIdToRedisKey(deviceId)).map(_.toSet[String])

    override def addNeighborForDevice(deviceId: String, nbrId: String): Future[String] = redisClient.sadd(deviceIdToRedisKey(deviceId), nbrId).map[String](_ => deviceId)

    override def removeNeighborForDevice(deviceId: String, nbrId: String): Future[String] = ???

    override def getNbrsSpatial(deviceId: String): Future[Set[String]] = {
      redisClient.geoRadiusByMember("netSvc:spatial", deviceId, 100).map(_.toSet).recover {
        case _ => Set()
      }
    }

    override def updatePosition(deviceId: String, latitude: String, longitude: String): Future[Boolean] = redisClient.geoAdd("netSvc:spatial", latitude.toDouble, longitude.toDouble, deviceId).map(_ => true)

    override def getDevicesByBoundingBox(boundingBox: BoundingBox): Future[Seq[String]] = {
      redisClient.geoAdd("temp", boundingBox.leftTop.lat, boundingBox.leftTop.lon, "leftTop")
      redisClient.geoAdd("temp", boundingBox.rightBotom.lat, boundingBox.rightBotom.lon, "rightBottom")

      //val radius = redisClient.geoDist("temp", "leftTop", "rightBottom").map(_ / 2)
      val radius = distanceBetweenTwoCoordinates(latLon1 = boundingBox.leftTop, latLon2 = boundingBox.rightBotom)

      val center = getCentralGeoCoordinate(Set(boundingBox.leftTop,boundingBox.rightBotom))

      redisClient.geoRadius("netSvc:spatial",center.lat,center.lon,radius,DistUnits.Meter)
    }

    private def getCentralGeoCoordinate(geoCoordinates:Set[LatLon]): LatLon =
    {
      if (geoCoordinates.size == 1)
      {
        return geoCoordinates.head
      }

      var x : Double = 0
      var y : Double = 0
      var z : Double = 0

      geoCoordinates.foreach{geoCoordinate =>

        val latitude = geoCoordinate.lat * Math.PI / 180
        val longitude = geoCoordinate.lon * Math.PI / 180

        x += Math.cos(latitude) * Math.cos(longitude)
        y += Math.cos(latitude) * Math.sin(longitude)
        z += Math.sin(latitude)
      }

      val total = geoCoordinates.size

      x = x / total
      y = y / total
      z = z / total

      val centralLongitude = Math.atan2(y, x)
      val centralSquareRoot = Math.sqrt(x * x + y * y)
      val centralLatitude = Math.atan2(z, centralSquareRoot)

      LatLon((centralLatitude * 180 / Math.PI).toFloat, (centralLongitude * 180 / Math.PI).toFloat)
    }

    private def distanceBetweenTwoCoordinates(latLon1: LatLon,latLon2: LatLon) = {
      var R = 6371e3; // metres
      var φ1 = latLon1.lat * Math.PI / 180
      var φ2 = latLon2.lat * Math.PI / 180
      var Δφ = (latLon2.lat-latLon1.lat) * Math.PI / 180
      var Δλ = (latLon2.lon -latLon1.lon) * Math.PI / 180

      var a = Math.sin(Δφ/2) * Math.sin(Δφ/2) + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ/2) * Math.sin(Δλ/2)
      var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))

      R * c
    }
  }
}
