package com.taxico.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.taxico.gps.Location
import com.taxico.proximity.ProximityCalculation.isNearSources


/**
 * Created by richardgibson
 * 24/01/15
 */

object TubeLocationActor {

  def props[T](tubeLocations:Set[Location], proximityRadius:Double)
    = Props(classOf[TubeLocationActor], tubeLocations,proximityRadius)
}

/**
 *
 * @param tubeLocations Set of all Tube Locations used by TubeLocationActor to check against Taxi Location
 * @param maxProximity maximum distance for Taxi to be deemed close to Tube Station
 */
class TubeLocationActor(tubeLocations:Set[Location], maxProximity:Double)
  extends Actor with ActorLogging{

  val isNearTubeStation = isNearSources(maxProximity)(tubeLocations)
  override def receive:Receive = {
    case CloseToTubeStation(location:Location) =>
      sender ! CloseToTubeStationResponse(!isNearTubeStation(location).isEmpty , location)
  }
}
