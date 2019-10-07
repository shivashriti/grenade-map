package com.shriti.grenademap.actors

import akka.actor._

import com.shriti.grenademap.models._

/**
  * Types of messages State Actor can receive
  */
sealed trait StateMessage
case object GetState extends StateMessage
case class SetState(s: State) extends StateMessage

/**
  * State Actor maintains the state of the application
  * It is responsible for providing current step of the application and updating it to a new state
  * @param s0 the state of application
  */
class StateActor(s0: State) extends Actor {
  private var state: State = s0

  def receive = {
    case GetState    => sender ! state
    case SetState(s) => state = s
  }
}
