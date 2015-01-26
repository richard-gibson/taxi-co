package com.taxico.actors

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.specs2.mutable.After

/**
 * Created by richardgibson
 * 24/01/15
 */
abstract class AkkaTestkitSpecs2Support extends TestKit(ActorSystem("test-system", ConfigFactory.parseString( """akka.loggers = ["akka.testkit.TestEventListener"]""")))
with After
with ImplicitSender {

// shut down the actor system after tests have run
def after = system.shutdown()
}
