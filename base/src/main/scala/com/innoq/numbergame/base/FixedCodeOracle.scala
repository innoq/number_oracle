package com.innoq.numbergame.base

import com.innoq.numbergame.baseapi._

/**
 * A straightforward oracle that's based on a hidden code.
 */
class FixedCodeOracle(hiddenCode: Array[Int], base: Int) extends Oracle {
  val length = hiddenCode.length

  override def getBase() = base
  override def getLength() = length
  override def getType() = Oracle.OracleType.FAIR
  override def iGiveUp() = hiddenCode

  private def countColors(digits: Array[Int], err: Int => Unit): Array[Int] = {
    val result = new Array[Int](base)
    digits.foreach((d:Int) => 
      if(0 <= d && d < base) result(d) += 1
      else err(d))
    result
  }

  /** Cache this, to be a bit faster when called several times, with different attempts. */
  val hiddenCodeColors = countColors(
    hiddenCode, (badDigit: Int) => 
      throw new IllegalArgumentException("Bad digit " + badDigit + " in hidden code"))

  override def divinate(attempt: Array[Int]) = {
    if (attempt.length != hiddenCode.length)
       throw new BadAttemptException("Expecting " + hiddenCode.length + " in your attempt, found " + attempt.length)
    var fullMatches = 0
    (hiddenCode zip attempt).foreach {
      case (dh:Int, da:Int) => if(dh == da) fullMatches += 1 }
    val attemptColors = 
      countColors(
        attempt, 
        (badDigit: Int) => throw new BadAttemptException
          ("Bad digit " + badDigit + " found in your attempt, expecting 0 .. " + base))
    var colorMatches = 0
    (hiddenCodeColors zip attemptColors).foreach {
      case(a:Int, b:Int) => colorMatches += (if(a < b) a else b) }
    new OracleResult(fullMatches, colorMatches - fullMatches)
  }
}
