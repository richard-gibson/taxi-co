package com.taxico.actors

import akka.actor.{Props, ActorRef}
import akka.testkit.{EventFilter, TestProbe}
import com.taxico.gps.Location
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

/**
 * Created by richardgibson
 * 24/01/15
 */
class TaxiSpec extends Specification with Mockito {

  class UnScheduledTaxi(val managementCentre:ActorRef, val tubeLocator:ActorRef, val gpsProps: Props, gpsActorRef:ActorRef)
    extends Taxi with Scheduler {
      override val gps = gpsActorRef
      override def startScheduler() = {}
      override def stopScheduler() = {}
  }

  "Taxi Actor" should {
    "emit a RetrieveLocation to GPS actor if Taxi in Default State when RefreshLocation msg received" in new AkkaTestkitSpecs2Support {
      val gpsProbe = TestProbe()
      val managementCentre = mock[ActorRef]
      val tubeLocator = mock[ActorRef]
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentre, tubeLocator, Props.empty, gpsProbe.ref)))

      taxiActor ! RefreshLocation
      gpsProbe.expectMsg(RetrieveLocation)

    }

    "emit a RetrieveLocation to GPS actor if Taxi in Tube Station Aware State when RefreshLocation msg received" in new AkkaTestkitSpecs2Support {
      val gpsProbe = TestProbe()
      val managementCentre = mock[ActorRef]
      val tubeLocator = mock[ActorRef]
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentre, tubeLocator, Props.empty, gpsProbe.ref)))

      taxiActor ! TubeStationAwareTaxi
      taxiActor ! RefreshLocation
      gpsProbe.expectMsg(RetrieveLocation)

    }

    "emit CurrentLocation to Management Centre if Taxi in Default State when CurrentLocation msg received"  in new AkkaTestkitSpecs2Support {
      val gps = mock[ActorRef]
      val managementCentreProbe = TestProbe()
      val tubeLocator = mock[ActorRef]
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentreProbe.ref, tubeLocator, Props.empty, gps)))

      val currentLocation = CurrentLocation(Success(Location(3.0,4.0)))
      taxiActor ! currentLocation
      managementCentreProbe.expectMsg(currentLocation)

    }

    "emit CurrentLocation to Tube Locator when CurrentLocation msg received, " +
      "if Taxi in Tube Station Aware State and CurrentLocation contains Success[Location] type"   in new AkkaTestkitSpecs2Support {
      val gps = mock[ActorRef]
      val managementCentre = mock[ActorRef]
      val tubeLocatorProbe = TestProbe()
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentre, tubeLocatorProbe.ref, Props.empty, gps)))

      val location = Location(3.0,4.0)
      val currentLocation = CurrentLocation(Success(location))
      val closeToTubeStation = CloseToTubeStation(location)

      //change to Tube Station Aware
      taxiActor ! TubeStationAwareTaxi
      taxiActor ! currentLocation
      tubeLocatorProbe.expectMsg(closeToTubeStation)

    }

    "emit CloseToTubeStationResponse to Management Centre when CloseToTubeStationResponse msg received, " +
      "if Taxi in Tube Station Aware State and location is close to Tube station" in new AkkaTestkitSpecs2Support {
      val gps = mock[ActorRef]
      val managementCentreProbe = TestProbe()
      val tubeLocator = mock[ActorRef]
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentreProbe.ref, tubeLocator, Props.empty, gps)))

      val location = Location(3.0,4.0)
      val closeToTubeStationResponse = CloseToTubeStationResponse(true, location)
      //change to Tube Station Aware
      taxiActor ! TubeStationAwareTaxi
      taxiActor ! closeToTubeStationResponse
      managementCentreProbe.expectMsg(closeToTubeStationResponse)

    }

    "does not emit CloseToTubeStationResponse msg when received, " +
      "if Taxi in Tube Station Aware State and location is not close to Tube station" in new AkkaTestkitSpecs2Support {
      val gps = mock[ActorRef]
      val managementCentreProbe = TestProbe()
      val tubeLocator = mock[ActorRef]
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentreProbe.ref, tubeLocator, Props.empty, gps)))

      val location = Location(3.0,4.0)
      val notCloseToTubeStationResponse = CloseToTubeStationResponse(false, location)
      //change to Tube Station Aware
      taxiActor ! TubeStationAwareTaxi
      taxiActor ! notCloseToTubeStationResponse
      managementCentreProbe.expectNoMsg(FiniteDuration(1, "second"))

    }

    "logs error when CloseToTubeStationResponse msg  received, " +
      "if Taxi in Default State and location is close to Tube station" in new AkkaTestkitSpecs2Support {
      val gps = mock[ActorRef]
      val managementCentre = mock[ActorRef]
      val tubeLocator = mock[ActorRef]
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentre, tubeLocator, Props.empty, gps)))

      val location = Location(3.0,4.0)
      val closeToTubeStationResponse = CloseToTubeStationResponse(true, location)

      val message = "Unable to process CloseToTubeStationResponse messages in current state"
      EventFilter.error(message = message, occurrences = 1) intercept {
        taxiActor ! closeToTubeStationResponse
      }


    }

    "emit CloseToTubeStationResponse to Management Centre when CloseToTubeStationResponse msg received, " +
      "if Taxi in Tube Station Aware State and location is close to Tube station" in new AkkaTestkitSpecs2Support {
      val gps = mock[ActorRef]
      val managementCentreProbe = TestProbe()
      val tubeLocator = mock[ActorRef]
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentreProbe.ref, tubeLocator, Props.empty, gps)))

      val location = Location(3.0,4.0)
      val closeToTubeStationResponse = CloseToTubeStationResponse(true, location)
      //change to Tube Station Aware
      taxiActor ! TubeStationAwareTaxi
      taxiActor ! closeToTubeStationResponse
      managementCentreProbe.expectMsg(closeToTubeStationResponse)

    }

    "emit CurrentLocation to Management Centre when CurrentLocation msg received, " +
      "if Taxi in Tube Station Aware State and CurrentLocation contains Failure type" in new AkkaTestkitSpecs2Support {
      val gps = mock[ActorRef]
      val managementCentreProbe = TestProbe()
      val tubeLocator = mock[ActorRef]
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentreProbe.ref, tubeLocator, Props.empty, gps)))

      val currentLocation = CurrentLocation(Failure(new Exception))

      //change to Tube Station Aware
      taxiActor ! TubeStationAwareTaxi
      taxiActor ! currentLocation
      managementCentreProbe.expectMsg(currentLocation)

    }

    "return to default behaviour from Tube Station Aware State when DefaultTaxi msg received"  in new AkkaTestkitSpecs2Support {
      val gpsProbe = TestProbe()
      val managementCentreProbe = TestProbe()
      val tubeLocatorProbe = TestProbe()
      val taxiActor = system.actorOf(Props(new UnScheduledTaxi(managementCentreProbe.ref, tubeLocatorProbe.ref, Props.empty, gpsProbe.ref)))

      val location = Location(3.0,4.0)
      val currentLocation = CurrentLocation(Success(location))
      val closeToTubeStation = CloseToTubeStation(location)

      //change to Tube Station Aware
      taxiActor ! TubeStationAwareTaxi
      //confirm actor is using Tube Station Aware behaviour
      taxiActor ! currentLocation
      tubeLocatorProbe.expectMsg(closeToTubeStation)

      //change to default behaviour
      taxiActor ! DefaultTaxi

      taxiActor ! RefreshLocation
      gpsProbe.expectMsg(RetrieveLocation)

      taxiActor ! currentLocation
      managementCentreProbe.expectMsg(currentLocation)

    }
  }

}
