package com.shriti.grenademap.actors

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.shriti.grenademap.models._
import com.shriti.grenademap.util.BlastUtil._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Types of messages BlastActor can receive
  */
sealed trait BlastMessage
case class StartBlast(blast: Blast) extends BlastMessage
case class SpawnPerson(person: Person) extends BlastMessage
case object NextStep extends BlastMessage

/**
  * Blast Actor takes care of the important tasks of server
  * like initiate blast, spawn person, generate next step, detect end etc.
  * @param stateActor
  */
class BlastActor (stateActor: ActorRef) extends Actor {

  import BlastActor._
  implicit val timeout = Timeout(actionTimeout)

  def receive = {
    case s: StartBlast => updateState(initiate(s.blast))
    case s: SpawnPerson => updateState(spawn(s.person))
    case NextStep => updateState(next)
  }

  /**
    * Generic function to update state for all messages
    * @param f
    */
  private[this] def updateState(f: State => State) {
    (stateActor ? GetState).mapTo[State].map(state =>
      stateActor ! SetState(f(state))
    )
  }

  private[this] def next = {state: State =>
    val (isEnd, newState) = BlastActor.next(state)
    if(isEnd)
      context.stop(self)
    newState
  }
}

/**
  * Contains utility methods for tasks that change state of server
  */
object BlastActor {

  /**
    * Generates new step's state
    * @return
    */
  private[grenademap] def next = { state: State =>
    val newState =
      checkDeaths(
        State(
          persons = moveOrNot(removeDead(state.persons)),
          blasts = propagate(state.blasts),
          status = Active,
          serverResponses = Nil,
          clientMessages = Nil
        )
      )
    val isEnd = checkIfNoneAlive(newState)
    if(isEnd)
      (isEnd, newState.copy(
        status = End,
        serverResponses = endMessage +: newState.serverResponses)
      )
    else (isEnd, newState)
  }

  /**
    * Initiates a new blast and produces appropriate state
    * @param blast  to initiate
    * @param state  current state
    * @return
    */
  private[grenademap] def initiate(blast: Blast)(state: State): State =
    state.copy(
      blasts = blast +: state.blasts,
      serverResponses = Nil,
      clientMessages = (blast.block.position._1, blast.block.position._2, blast.radius) +: state.clientMessages
    )

  /**
    * Attempts to spawn a new person and produces appropriate state
    * @param person to spawn
    * @param state current state
    * @return
    */
  private[grenademap] def spawn(person: Person)(state: State): State =
    if (isPositionOccupied(person.block, state))
      state.copy(
        serverResponses = Seq(spawnErrorMessage),
        clientMessages = (person.block.position._1, person.block.position._2, -1) +: state.clientMessages
      )
    else
      state.copy(
        persons = person +: state.persons,
        serverResponses = Nil,
        clientMessages = (person.block.position._1, person.block.position._2, -1) +: state.clientMessages
      )

}