package com.taxico

import akka.actor.{ActorRef, ActorSystem}
import com.taxico.actors._
import com.taxico.gps.{Location, MockSatelliteService}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.io.StdIn

/**
 * Created by richardgibson
 * 24/01/15
 */
object TaxiCoApp extends App {

  val system = ActorSystem("Taxi-co-system")

  val GPSRefresh  = 1 second
  val taxiRefresh = 2 seconds
  val noOfTaxis = 10
  val conf = ConfigFactory.load()
  val locationsPath = conf.getString("locations.file.path")
  val satelliteService = new MockSatelliteService(locationsPath)

  val tubeStationLocations = Set(
    Location(51.5315,-0.10194),   Location(51.53646,-0.09968),
    Location(51.52793,-0.09495),  Location(51.51285,-0.09802),
    Location(51.53369,-0.10083),  Location(51.53345,-0.09938),
    Location(51.52737,-0.09947),  Location(51.54963,-0.10057),
    Location(51.51564,-0.09837),  Location(51.53642,-0.09489),
    Location(51.55,-0.09699),     Location(51.53823,-0.09822),
    Location(51.53321,-0.09808),  Location(51.52902,-0.10127),
    Location(51.51962,-0.09371),  Location(51.53066,-0.09929),
    Location(51.5237,-0.09534),   Location(51.51661,-0.09412),
    Location(51.52367,-0.09418),  Location(51.54206,-0.10181),
    Location(51.51585,-0.10311))

  val proximityRadius = 1.0

  val gpsProps = AkkaScheduledGPS.props(satelliteService,GPSRefresh)

  val managementCentre = system.actorOf(ManagementCentre.props, name = "Management-Centre")
  val tubeLocator = system.actorOf(TubeLocationActor.props(tubeStationLocations,proximityRadius), name = "Tube-Location-Actor")

  val taxis:List[ActorRef] = (1 to noOfTaxis).toList.map(
    taxiNumber =>
      system.actorOf(AkkaScheduledTaxi.props(managementCentre, tubeLocator, gpsProps, taxiRefresh), name = s"Taxi-$taxiNumber")
    )

  import system.dispatcher
  //schedule message to be sent to taxis to change to tube station aware behaviour after 10 seconds
  taxis foreach(system.scheduler.scheduleOnce(10 second,_,TubeStationAwareTaxi))

  StdIn.readLine(f"Hit ENTER to exit ...%n")
  system.shutdown()
  system.awaitTermination()

}
