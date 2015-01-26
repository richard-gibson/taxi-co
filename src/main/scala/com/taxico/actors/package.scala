package com.taxico

import com.taxico.gps.Location

import scala.util.Try

/**
 * Created by richardgibson
 * 24/01/15
 */
package object actors {

  sealed trait LocationMsg
  object RetrieveLocation extends LocationMsg
  object RefreshLocation extends LocationMsg
  case class CurrentLocation(location:Try[Location]) extends LocationMsg

  sealed trait TubeLocationMsg
  case class CloseToTubeStation(loc: Location) extends TubeLocationMsg
  case class CloseToTubeStationResponse(isClose:Boolean, loc: Location) extends TubeLocationMsg

  case class Degrees(value:Double)

  sealed trait TaxiBehaviour
  object DefaultTaxi extends TaxiBehaviour
  object TubeStationAwareTaxi extends TaxiBehaviour
}
