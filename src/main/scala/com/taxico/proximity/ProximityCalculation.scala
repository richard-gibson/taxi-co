package com.taxico.proximity

import com.taxico.gps.Location

/**
 * Created by richardgibson
 * 24/01/15
 *
 *
 */

object ProximityCalculation {
  import math._
  val earthRadius = 6372.8  //radius in km

  /**
   * Haversine function used to calculate distance between 2 coordinates
   * source http://rosettacode.org/wiki/Haversine_formula#Scala
   * @return
   */
  def haversine(sourceLocation:Location, objectLocation:Location) ={

    val dLat=(sourceLocation.latitude - objectLocation.latitude).toRadians
    val dLon=(sourceLocation.longitude - objectLocation.longitude).toRadians

    val a = pow(sin(dLat/2),2) + pow(sin(dLon/2),2) *
        cos(sourceLocation.latitude.toRadians) * cos(objectLocation.latitude.toRadians)

    val c = 2 * asin(sqrt(a))
    earthRadius * c
  }

  val isNearSources: Double => Set[Location] => Location => Set[Location] =
    proximityDistance => sourceLocations => objectLocation =>
      sourceLocations filter (sourceLocation =>
        haversine(sourceLocation, objectLocation) <= proximityDistance)

}
