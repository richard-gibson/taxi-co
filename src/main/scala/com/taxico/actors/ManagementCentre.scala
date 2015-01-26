package com.taxico.actors

import akka.actor.{Props, ActorLogging, Actor}
import com.taxico.gps.{SatServiceUnavailableException, Location}

import scala.util.{Failure, Success}

/**
 * Created by richardgibson
 * 24/01/15
 * ManagementCentre Actor process and logs content for CurrentLocation and
 * CloseToTubeStationResponse messages
 *
 */

object ManagementCentre {
  def props = Props(classOf[ManagementCentre])
}

class ManagementCentre extends Actor with ActorLogging {
  override def receive: Receive = {
    case CurrentLocation(locationT) => locationT match {
      case Success(location:Location) =>
        log.info(s"${sender.path.name} currently at coordinates ${location.latitude},${location.longitude}")
      case Failure(e:SatServiceUnavailableException) =>
        log.info(s"${sender.path.name} location currently unavailable")
      case Failure(t:Throwable) =>
        log.error(s"Unhandled error experienced by Taxi [${sender.path.name}}], msg = ${t.getMessage}")
    }
    case CloseToTubeStationResponse(true,location) =>
      log.info(s"${sender.path.name} close to Tube Station at coordinates ${location.latitude},${location.longitude}")
  }
}
