package com.shriti.grenademap.models

import akka.actor.{ActorRef, Cancellable, Scheduler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

// generic Timer
trait Timer {
  protected var cancellable: Cancellable = _

  def start(): Unit
  def stop(): Unit = {
    if (cancellable != null && !cancellable.isCancelled) {
      cancellable.cancel()
    }
  }
  def restart(): Unit = {
    stop()
    start()
  }
}

/**
  * Generic Custom Timer that schedules an event at regular intervals
  * @param min  minimum duration to trigger event
  * @param max  maximum duration to perform event
  * @param event  event to perform
  * @param scheduler  scheduler
  * @param target receiver actor's reference
  * @param sender sender actor's reference
  */
class RandomizedTimer(min: FiniteDuration, max: FiniteDuration, event: Any)(
  implicit scheduler: Scheduler,
  target: ActorRef,
  sender: ActorRef
) extends Timer {
  def start(): Unit = {
    require(target != null, "Timer target can not be null")
    val rd = min.toMillis + Random.nextInt((max.toMillis - min.toMillis + 1).toInt)
    cancellable = scheduler.schedule(Client.initialDelay, rd milliseconds, target, event)(global, sender)
  }
}
