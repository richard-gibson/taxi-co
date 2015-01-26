package com.taxico.actors

import akka.actor.{ActorLogging, Actor, Props, ActorRef}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Taxi Actor used to model Taxi sending location data to Management Centre
 * Taxi Actor has 2 states
 * 1. Default State: Interacts with Management Centre and GPS only
 *    (see Default Taxi Behaviour)
 * 2. Tube Station Aware State: Interacts with Management Centre and GPS
 *    (see Tube Station Aware Taxi Behaviour)
 */

trait Taxi extends Actor with ActorLogging {
  this: Scheduler =>

  protected val gpsProps: Props
  protected val managementCentre:ActorRef
  protected val tubeLocator:ActorRef

  protected val gps = context.actorOf(gpsProps, name = "GPS")

  override def preStart() = {
    startScheduler()
  }

  override def postStop() = {
    stopScheduler()
  }

  override def receive: Receive = defaultTaxiBehaviour

  def defaultTaxiBehaviour: Receive =
    retrieveLocationToGPS orElse currentLocationToManagementCentre orElse switchToTubeAwareBehaviour orElse killCloseToTubeStationResponse

  def tubeAwareTaxiBehaviour: Receive =
    retrieveLocationToGPS orElse currentLocationToTubeLocator orElse closeToTubeStationResponseToManagementCentre orElse switchToDefaultBehaviour


  def retrieveLocationToGPS: Receive = {
    case RefreshLocation =>
      gps ! RetrieveLocation
  }

  def currentLocationToManagementCentre: Receive = {
    case currentLocation:CurrentLocation =>
      managementCentre ! currentLocation
  }

  def currentLocationToTubeLocator: Receive = {
    case currentLocation@CurrentLocation(locT) => locT match {
      case Success(loc) => tubeLocator ! CloseToTubeStation(loc)
      //If location failure notify management centre as in default behaviour
      case Failure(_) => managementCentre ! currentLocation
    }
  }

  def closeToTubeStationResponseToManagementCentre: Receive = {
    case closeToTubeStationResponse@CloseToTubeStationResponse(true, _) =>
      managementCentre ! closeToTubeStationResponse
    case CloseToTubeStationResponse =>
      log.debug(s"${self.path.name} not in proximity of Tube Station")
  }

  def killCloseToTubeStationResponse: Receive = {
    case CloseToTubeStationResponse(_,_) =>
      log.error("Unable to process CloseToTubeStationResponse messages in current state")
  }

  def switchToTubeAwareBehaviour: Receive = {
    case TubeStationAwareTaxi =>
      log.info("Changing Taxi Behaviour to check Tube Station Proximity")
       context become tubeAwareTaxiBehaviour
  }

  def switchToDefaultBehaviour: Receive = {
    case DefaultTaxi =>
      log.info("Changing Taxi Behaviour to ignore Tube Station Proximity")
      context become defaultTaxiBehaviour
  }

}


object AkkaScheduledTaxi {
  def props(managementCentre:ActorRef, tubeLocator:ActorRef, gpsProps: Props, pollingInterval:FiniteDuration)
  = Props(classOf[AkkaScheduledTaxi], managementCentre, tubeLocator, gpsProps, pollingInterval)
}

/**
 *
 * AkkaScheduledTaxi mixes Taxi with AkkaMsgScheduler to schedule messages to self
 *
 * @param managementCentre
 * @param tubeLocator
 * @param gpsProps
 * @param pollingInterval
 */
class AkkaScheduledTaxi(val managementCentre:ActorRef, val tubeLocator:ActorRef, val gpsProps: Props, val pollingInterval:FiniteDuration)
  extends Taxi with AkkaMsgScheduler {
  override val initialDelay = pollingInterval
  override val scheduledMsg = RefreshLocation
  override val interval = pollingInterval
  override val receiver = self

}
