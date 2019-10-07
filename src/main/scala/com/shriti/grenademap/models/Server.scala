package com.shriti.grenademap.models

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.shriti.grenademap.actors._
import com.shriti.grenademap.util.BlastUtil._

import scala.concurrent._
import ExecutionContext.Implicits.global

/**
  * Server initializes all necessary actors and schedules them as necessary
  * It provides the current step's view to update the UI regularly.
  */
class Server {

  implicit val timeout = Timeout(actionTimeout)
  private[this] val system = ActorSystem("GrenadeMap")

  private[this] val initialState = State(Seq(initialPerson), Nil, Active)

  private[this] val stateActor = system.actorOf(Props(new StateActor(
    initialState)), name = "stateActor")

  private[this] val blastActor = system.actorOf(Props(new BlastActor(
    stateActor)), name = "blastActor")

  private[this] val mainActor = system.actorOf(Props(new MainActor(
    blastActor)), name = "mainActor")

  // client to generate targets
  private[this] val client = system.actorOf(Props(new Client), name = "client")

  // scheduler to generate next step at regular intervals
  private[this] val stepTimer = system.scheduler.schedule(
    initialDelay, stepInterval, blastActor, NextStep)

  // scheduler to generate random targets at random intervals
  private[this] val automatedClient =
    new RandomizedTimer(Client.minDuration, Client.maxDuration, Target)(system.scheduler, client, mainActor)

  // start the automated client
  automatedClient.start()

  // prepares view for current step
  def presentView: View = {
    val state = Await.result((stateActor ? GetState).mapTo[State], timeout.duration)
    View(
      state.persons.map(_.block),
      state.blasts.flatMap(generateBlastArea),
      state.serverResponses,
      state.clientMessages.map(_.toString)
    )
  }

  // additional utilities to be implemented
  def pause() = ???
  def quit() = ???
  def restart() = ???
}
