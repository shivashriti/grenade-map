package com.shriti.grenademap.actors

import akka.actor._
import akka.util.Timeout
import com.shriti.grenademap.models._

/**
  * MainActor receives targets from client, interprets them and tells BlastActor to handle accordingly
  * @param blastActor  reference of BlastActor
  */
class MainActor(blastActor: ActorRef) extends Actor {
  implicit val timeout = Timeout(actionTimeout)

  def receive = {
    case ClientMessage(t) => handleClientMessage(t)
  }

  private def handleClientMessage(targets: Seq[(Int, Int, Int)]) = {
    // segregate blasts and person from the targets
    val (persons, blasts) =
      targets
        .foldLeft((Seq[Person](), Seq[Blast]()))((lists, t) => {
          t match {
            case (x, y, r) if r < 0 => (Person(Block(x, y), 0) +: lists._1, lists._2)
            case (x, y, r) => (lists._1, Blast(Block(x, y), r) +: lists._2)
          }
        })

    // tells to start blasts
    blasts
      .map(b =>
        blastActor ! StartBlast(b)
      )

    // tells to spawn persons
    persons
      .map(p => {
        blastActor ! SpawnPerson(
          p.copy(
            canJumpAfter = if (blasts.nonEmpty) blasts(0).radius else 0
          )
        )
      })
  }
}
