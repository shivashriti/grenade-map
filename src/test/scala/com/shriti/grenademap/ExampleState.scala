package com.shriti.grenademap

import com.shriti.grenademap.models._

trait ExampleState {

  // blast that has not propagated
  val b0 = Blast(Block(3,3), 2, 0)

  // another blast
  val b1 = Blast(Block(9,9), 4, 0)

  // blast that is in middle of propagation
  val b2 = Blast(Block(3,3), 5, 2)

  // person that can jump
  val p0 = Person(Block(4,4), 0)

  // person that not jump till 2 more steps
  val p1 = Person(Block(4,4), 2)

  // another person that can jump
  val p2 = Person(Block(8,8), 0)

  // state with un propagated blast
  val s0 = State(Nil, Seq(b0), Active, Nil, Nil)

  // state with person that can jump
  val s1 = State(Seq(p0), Nil,Active, Nil, Nil)

  // state with person that can not jump at the moment
  val s2 = State(Seq(p1), Nil,Active, Nil, Nil)

  // state with person standing adjacent to a propagating blast
  val s3 = State(Seq(p1), Seq(b2),Active, Nil, Nil)
}
