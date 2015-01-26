package com.taxico

/**
 * Created by richardgibson
 * 26/01/15
 */
package object gps {
  case class Location(latitude:Double,longitude:Double)
  case class SatServiceUnavailableException(msg: String) extends Exception(msg)
}
