package com.innoq.numbergame.permutation

import org.junit._
import org.junit.Assert

class PermutationTest {
  @Test
  def test = {
    val p = new Permutation(15)
    (0 until 15).foreach((i:Int) => {
      Assert.assertEquals(i, p.unapply(p.apply(i)))
      Assert.assertEquals(i, p.apply(p.unapply(i)))
    })
  }
}
