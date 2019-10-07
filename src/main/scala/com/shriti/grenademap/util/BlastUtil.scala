package com.shriti.grenademap.util

import com.shriti.grenademap.models._

import scala.util.Random

/**
  * Utilities
  */
private[grenademap] object BlastUtil {

  // gives all the blocks that given blast covers at a step
  def generateBlastArea(blast: Blast): Seq[Block] = {
    val x = blast.block.position._1
    val y = blast.block.position._2
    val r = blast.propagationStep

    if (r <= blast.radius) {
      (x-r to x+r).foldLeft(Seq[Block]())((blocks, i) => {
        blocks ++
          (y-r to y+r).foldLeft(Seq[Block]())((blocks_row, j) => {
            Block(i, j) +: blocks_row
          })
      })
        .filter {
          case Block((x, y)) =>
              x >= 0 &&
              y >= 0 &&
              x < gridSize._1 &&
              y < gridSize._2
        }
    }
    else Seq()
  }

  // handles persons movements
  def moveOrNot(persons: Seq[Person]): Seq[Person] =
    persons
      .foldLeft(Seq[Person]())((personList, p) => {
        // track when person will be able to jump if can't jump now
        if (p.canJumpAfter > 0)
          p.copy(canJumpAfter = p.canJumpAfter - 1) +: personList
        else    // jump
          p.copy(block = randomJump(p.block)) +: personList
      })

  // removes persons died in previous step
  def removeDead(persons: Seq[Person]): Seq[Person] =
    persons
      .filter(_.isAlive)

  // propagates a blast when not finished else remove it
  def propagate(blasts: Seq[Blast]): Seq[Blast] =
    blasts.foldLeft(Seq[Blast]())((blastList, b) => {
      if (b.propagationStep <= b.radius)
        b.copy(propagationStep = b.propagationStep + 1) +: blastList
      else blastList
    })

  // gives a random block for jump
  def randomJump(position: Block): Block = {

    // TODO: restrict the random jump based on pre-configured limit
    Block(
      Random.nextInt(gridSize._1),
      Random.nextInt(gridSize._2)
    )
  }

  // checks if person stands in blast area and mark dead
  def markDead(blasts: Seq[Blast], persons: Seq[Person]): Seq[Person] = {
    val totalBlastArea = blasts.flatMap(generateBlastArea)
    persons.map(p => {
      if(totalBlastArea.contains(p.block))
        p.copy(isAlive = false)
      else p
    })
  }

  // checks if someone dies at a step and add server responses
  def checkDeaths(state: State): State = {
    val allPersonsMarked = markDead(state.blasts, state.persons)
    val numOfPersonsKilled = allPersonsMarked.count(!_.isAlive)

    if (numOfPersonsKilled > 0)
      state.copy(
        persons = allPersonsMarked,
        serverResponses = (1 to numOfPersonsKilled)
          .foldLeft(state.serverResponses)((pList, _) => killedMessage +: pList)
      )
    else state
  }

  // is this block occupied at this time by another person
  def isPositionOccupied(block: Block, state: State): Boolean =
    state.persons.map(_.block).contains(block)

  // check if this step contains any person alive
  def checkIfNoneAlive(state: State): Boolean = {
    val aliveList = state.persons.filter(_.isAlive)
    aliveList.size == 0
  }
}
