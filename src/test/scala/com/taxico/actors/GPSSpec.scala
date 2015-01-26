package com.taxico.actors

import akka.testkit.TestActorRef
import com.taxico.gps.{Location, SatelliteService}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification


import scala.util.Success

/**
 * Created by richardgibson
 * 24/01/15
 */
class GPSSpec extends Specification with Mockito {

  class UnScheduledGPS(val satelliteService:SatelliteService) extends GPS with Scheduler {
    override def startScheduler() = {}
    override def stopScheduler() = {}
  }

  "GPS Actor" should {

    val currentLocation1 = Success(Location(1.0,2.0))
    val currentLocation2 = Success(Location(3.0,4.0))

    "returns a Location after start up" in new AkkaTestkitSpecs2Support {

      val satelliteService:SatelliteService = mock[SatelliteService]
      satelliteService.retrieveLocation returns currentLocation1
      val gpsActor = TestActorRef(new UnScheduledGPS(satelliteService))

      gpsActor ! RetrieveLocation
      expectMsg(CurrentLocation(currentLocation1))
    }

    "refreshes Location after RefreshLocation sent" in new AkkaTestkitSpecs2Support {

      val satelliteService:SatelliteService = mock[SatelliteService]
      satelliteService.retrieveLocation returns currentLocation1 thenReturns currentLocation2
      val GPSActor = TestActorRef(new UnScheduledGPS(satelliteService))

      GPSActor ! RefreshLocation
      GPSActor ! RetrieveLocation
      expectMsg(CurrentLocation(currentLocation2))

    }

  }
}
