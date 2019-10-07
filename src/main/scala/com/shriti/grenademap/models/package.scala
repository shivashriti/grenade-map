package com.shriti.grenademap

import scala.concurrent.duration._

package object models {

  // constraints on grid size, grenade and person movements
  val gridSize: (Int,Int) = (60,60)
  val maxGrenadeRadius = 15
  val maxJumpRadius = 10

  // time intervals for steps and actions of server
  val stepInterval = 1 seconds
  val initialDelay = 0 seconds
  val actionTimeout = 2 seconds
  val drawInterval = 500

  // server responses
  val spawnErrorMessage = "Unable to Spawn person in that location"
  val killedMessage = "Person Killed"
  val endMessage = "End of the World!!!"

  // Application Status
  sealed trait AppStatus
  case object Active extends AppStatus
  case object End extends AppStatus

  // entities
  case class Block (position: (Int, Int))
  case class Person (block: Block, canJumpAfter: Int, isAlive: Boolean = true)
  case class Blast (block: Block, radius: Int, propagationStep: Int = 0)

  // to represent current step
  case class State(persons: Seq[Person],
                   blasts: Seq[Blast],
                   status: AppStatus,
                   serverResponses: Seq[String] = Nil,
                   clientMessages: Seq[(Int, Int, Int)] = Nil)

  // to draw current step's view
  case class View(persons: Seq[Block],
                  blastBlocks: Seq[Block],
                  serverResponses: Seq[String] = Nil,
                  clientMessages: Seq[String] = Nil)

  val initialPerson = Person(Block(gridSize._1 / 2, gridSize._2 / 2), 2)

  // time interval details for client
  object Client {
    val minDuration = stepInterval
    val maxDuration = stepInterval * 5
    val initialDelay = 3 seconds
  }
}
