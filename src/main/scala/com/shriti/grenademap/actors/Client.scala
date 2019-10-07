package com.shriti.grenademap.actors

import akka.actor._
import akka.util.Timeout
import com.shriti.grenademap.models._

import scala.concurrent.duration._
import scala.util.Random

case object Target
case class ClientMessage(target: Seq[(Int, Int, Int)])

/**
  * Client generates blind targets
  */
class Client extends Actor {
  implicit val timeout = Timeout(2000 millisecond)

  def receive = {
    case Target => generateBlindTarget()
  }

  // generate random blast and randomly decide to generate person
  def generateBlindTarget() = {

    // keeping the person targeting probability as 0.25
    val isSpawningPerson =
      if(Random.nextInt(4) == 0) true
      else false
    val grenades = Seq(targetGrenade())
    val persons = if(isSpawningPerson) Seq(targetPerson()) else Nil
    sender() ! ClientMessage(grenades ++ persons)
  }

  // give random blast target with random radius (in a configured limit)
  private def targetGrenade() =
    randomTarget(Random.nextInt(maxGrenadeRadius) + 1)

  // give random person target
  private def targetPerson() = randomTarget()

  private def randomTarget(grenadeRadius: Int = -1): (Int, Int, Int) =
    (
      Random.nextInt(gridSize._1),
      Random.nextInt(gridSize._2),
      grenadeRadius
    )
}

