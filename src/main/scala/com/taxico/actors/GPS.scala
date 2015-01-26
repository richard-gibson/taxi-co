package com.taxico.actors

import akka.actor._

import scala.concurrent.duration._
import scala.util.Try

import com.taxico.gps.{SatelliteService,Location}

/**
 *
 * GPS Actor used to retrieve and store current location for Satellite Service
 * Will store location as Try[Location], storing a failure if exception returned
 * from Satellite service
 *
 */


trait GPS extends Actor {
  this: Scheduler =>

  protected val satelliteService:SatelliteService

  private var location:Try[Location] = _

  override def preStart = {
    startScheduler
    location = satelliteService.retrieveLocation
  }

  override def receive: Receive = locate

  def locate:Receive = {
    case RefreshLocation =>
      location = satelliteService.retrieveLocation
    case RetrieveLocation =>
      sender ! CurrentLocation(location)

  }

  override def postStop = {
    stopScheduler
  }
}


object AkkaScheduledGPS {
  def props(satelliteService:SatelliteService, pollingInterval:FiniteDuration)
  = Props(classOf[AkkaScheduledGPS], satelliteService, pollingInterval)
}

class AkkaScheduledGPS(val satelliteService:SatelliteService, val pollingInterval:FiniteDuration) extends GPS with AkkaMsgScheduler {
  override val initialDelay = pollingInterval
  override val scheduledMsg = RefreshLocation
  override val interval = pollingInterval
  override val receiver = self

}
