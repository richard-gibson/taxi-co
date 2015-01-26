package com.taxico.actors

import com.taxico.gps.Location
import org.specs2.mutable.Specification


/**
 * Created by richardgibson
 * 24/01/15
 */
class TubeLocationActorSpec extends Specification {


  "TubeLocationActor" should {
    val tubeStationLocations = Set(Location(51.52797,-0.1033), Location(50.0005, 0.0002))
    val maxProximity = 14.5

    "Emit a positive response when sent a location in close proximity to a tube station" in new AkkaTestkitSpecs2Support {
      val tubeStationActor = system.actorOf(TubeLocationActor.props(tubeStationLocations,maxProximity))
      val taxiLocation = Location(51.52219,-0.30134)

      tubeStationActor ! CloseToTubeStation(taxiLocation)
      expectMsg(CloseToTubeStationResponse(isClose = true,taxiLocation))

    }

    "Emit a negative response when sent a location not in close proximity to a tube station" in new AkkaTestkitSpecs2Support {
      val tubeStationActor = system.actorOf(TubeLocationActor.props(tubeStationLocations,maxProximity))
      val taxiLocation = Location(10.123, -5.0110)

      tubeStationActor ! CloseToTubeStation(taxiLocation)
      expectMsg(CloseToTubeStationResponse(isClose = false,taxiLocation))

    }
  }

}