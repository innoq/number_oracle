package com.innoq.numbergame.permutation

import com.innoq.numbergame.baseapi.BadAttemptException

/**
 * This kind of an object permutes position and digits, but systematically so.
 */
class AttemptPermutation(length: Int, base: Int) {
  val digitP: Permutation = new Permutation(base)
  val positionP: Permutation = new Permutation(length)
  
  private def check(attempt: Array[Int]) = {
	  if(attempt.length != length)
      throw new BadAttemptException("Wrong attempt length, expected " + length + ", found " + attempt.length)
    attempt.foreach {digit => 
      if(digit < 0 || base <= digit)
        throw new BadAttemptException("Unexpected digit " + digit + ", expecte 0 .. " + (base-1))}
  }
  
  def apply(attempt: Array[Int]): Array[Int] = {
    check(attempt)
    val result = new Array[Int](length)
    (0 until length).foreach((i:Int) => result(positionP(i)) = digitP(attempt(i)))
    result
  }

  def unapply(attempt: Array[Int]): Array[Int] = {
    check(attempt)
    if(attempt.length != length)
      throw new BadAttemptException("Wrong attempt length, expected " + length + ", found " + attempt.length)
    val result = new Array[Int](length)
    (0 until length).foreach((i:Int) => result(i) = digitP.unapply(attempt(positionP(i))))
    result
  }
}
