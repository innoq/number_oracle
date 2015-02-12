package com.innoq.numbergame.permutation

import org.junit._
import org.junit.Assert

class AttemptPermutationTest {

  @Test
  def testSameInSameOut = {
    val dut = new AttemptPermutation(4, 5)
    val ori = Array(3,3,3,3)
    val permed = dut(ori)
    Assert.assertEquals(permed(0),permed(1))
    Assert.assertEquals(permed(0),permed(2))
    Assert.assertEquals(permed(0),permed(3))
  }

  @Test
  def testInverse = {
    val dut = new AttemptPermutation(4, 6)
    Array(Array(1,2,3,4), Array(0,0,0,1), Array(5,2,3,1), Array(2,2,4,4)).foreach { attempt =>
      Assert.assertArrayEquals(attempt, dut.unapply(dut(attempt)))
      Assert.assertArrayEquals(attempt, dut(dut.unapply(attempt)))}
  }
}
