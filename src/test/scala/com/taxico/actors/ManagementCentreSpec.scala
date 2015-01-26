package com.taxico.actors

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, EventFilter}
import com.taxico.gps.{SatServiceUnavailableException, Location}
import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification

import scala.util.{Failure, Success}

/**
 * Created by richardgibson
 * 26/01/15
 */
class ManagementCentreSpec extends Specification {



    "Management Centre Actor" should {

      "Log taxi location when Successful location received" in new AkkaTestkitSpecs2Support {

        val managementCentre = system.actorOf(ManagementCentre.props)

        val location = Location(3.0,4.0)
        val currentLocation = CurrentLocation(Success(location))
        val message = s".* currently at coordinates ${location.latitude},${location.longitude}"

        EventFilter.info(pattern = message, occurrences = 1) intercept {
           managementCentre ! currentLocation
        }

      }

      "Log taxi location close to Tube Station when CloseToTubeStationResponse msg received" in new AkkaTestkitSpecs2Support {

        val managementCentre = system.actorOf(ManagementCentre.props)

        val location = Location(3.0,4.0)
        val closeToTubeStationResponse = CloseToTubeStationResponse(true, location)
        val message = s".* close to Tube Station at coordinates ${location.latitude},${location.longitude}"

        EventFilter.info(pattern = message, occurrences = 1) intercept {
          managementCentre ! closeToTubeStationResponse
        }

      }

      "Log taxi location unavailable when GPS unable to connect to Satellite Service" in new AkkaTestkitSpecs2Support {

        val managementCentre = system.actorOf(ManagementCentre.props)

        val unavailableLocation = CurrentLocation(Failure(SatServiceUnavailableException("Connection to service unavailable")))
        val message = s".* location currently unavailable"

        EventFilter.info(pattern = message, occurrences = 1) intercept {
          managementCentre ! unavailableLocation
        }

      }

      "Log error if CurrentLocation containing Exception other than SatServiceUnavailableException received" in new AkkaTestkitSpecs2Support {

        val managementCentre = system.actorOf(ManagementCentre.props)

        val locationException = CurrentLocation(Failure(new Exception("")))
        val message = s"Unhandled error experienced by Taxi.*"

        EventFilter.error(pattern = message, occurrences = 1) intercept {
          managementCentre ! locationException
        }

      }

    }

}
