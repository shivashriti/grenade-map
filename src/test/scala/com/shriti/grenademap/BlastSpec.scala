package com.shriti.grenademap

import com.shriti.grenademap.actors.BlastActor._
import com.shriti.grenademap.models._

import org.specs2._

class BlastSpec extends Specification with ExampleState {def is =                         s2"""

 This is a specification to check Server's tasks

   Server should start new blast when asked                             $newBlast

   Server should spawn new person when asked                            $newPerson

   Server should propagate blast within grid                            $propagateBlast

   Server should change person's position on random jump                $movePerson

   Server should not move a person until grenade (that was targeted
   at same time as this person) finishes propagation                    $stopPerson

   Server should detect when person dies in a blast and notify          $markDead

   Server should remove the person that died in last step               $removeDead

   Server should detect when unable to spawn person and notify          $noSpawn

   Server should detect when all persons are dead and notify END        $detectEnd
                                                                        """

  def newBlast =
    initiate(b1)(s0).blasts.map(_.block) must contain(exactly(s0.blasts.head.block, b1.block))

  def newPerson =
    spawn(p2)(s1).persons.map(_.block) must contain(exactly(s1.persons.head.block, p2.block))

  def propagateBlast =
    next(s0)._2.blasts.head.propagationStep mustEqual s0.blasts.head.propagationStep + 1

  def movePerson =
    next(s1)._2.persons.head.block mustNotEqual s1.persons.head.block

  def stopPerson =
    next(s2)._2.persons.head.block mustEqual s1.persons.head.block

  def markDead =
    next(s3)._2.persons.head.isAlive mustEqual false
    next(s3)._2.serverResponses.head mustEqual killedMessage

  def removeDead =
    next(next(s3)._2)._2.persons mustEqual Nil

  def noSpawn =
    spawn(p0)(s1).serverResponses.head mustEqual spawnErrorMessage

  def detectEnd =
    next(s3)._2.persons.head.isAlive mustEqual false
    next(s3)._2.serverResponses.head mustEqual endMessage
}
