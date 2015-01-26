package com.taxico.gps

import com.typesafe.scalalogging.LazyLogging


import scala.util.{Failure, Try}

/**
 * Created by richard
 * on 21/01/15.
 */

trait SatelliteService {
  def retrieveLocation:Try[Location]
}

class MockSatelliteService(locationsPath:String) extends SatelliteService with LazyLogging {
  import scala.io.Source
  import scala.util.Random

  def locationsFromSource: Vector[Location] = try {
    val buf = scala.collection.mutable.ArrayBuffer.empty[Location]
    val sourceFile = Source.fromFile(locationsPath)
    sourceFile.getLines().foreach(
      line => {
        val cols = line.split(",")
        buf += Location(cols(0).toDouble, cols(1).toDouble)
      }
    )
    sourceFile.close()
    buf.to[Vector]
  } catch {
    case e:Throwable =>
      logger.error(s"Unable to load locations from $locationsPath: ${e.getMessage}")
      Vector.empty[Location]
  }
  lazy val locations:Vector[Location] = locationsFromSource
  lazy val noOfLocations:Int = locations.size

  override def retrieveLocation:Try[Location] =
    if(locations.isEmpty)
      Failure(SatServiceUnavailableException("Connection to service unavailable"))
    else
      Try(locations(Random.nextInt(noOfLocations)))
}


