package com.taxico.actors

import akka.actor.{ActorRef, Cancellable, Actor}

import scala.concurrent.duration.FiniteDuration

/**
 * Created by richardgibson
 * 24/01/15
 */

trait Scheduler {
  def startScheduler() : Unit
  def stopScheduler() : Unit
}

/**
 * Akka base message scheduler to be used as mixin
 */
trait AkkaMsgScheduler extends Scheduler {
  this:Actor =>

  protected val scheduledMsg:LocationMsg
  protected val initialDelay:FiniteDuration
  protected val interval:FiniteDuration
  protected val receiver:ActorRef

  private var scheduler: Cancellable = _

  override def startScheduler(): Unit = {
    import context.dispatcher
    scheduler = context.system.scheduler.schedule(
      initialDelay, interval, receiver, scheduledMsg
    )
  }

  override def stopScheduler(): Unit = {
    scheduler.cancel()
  }

}