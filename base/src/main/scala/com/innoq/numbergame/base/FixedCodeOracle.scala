package com.innoq.numbergame.base

import com.innoq.numbergame.baseapi._

/**
 * A straightforward oracle that's based on a hidden code.
 */
class FixedCodeOracle(hiddenCode: Array[Int], base: Int) extends Oracle {
  val length = hiddenCode.length
  def getBase(): Int = base
  def getLength(): Int = length
  def getType(): Oracle.OracleType = Oracle.OracleType.FAIR
  def iGiveUp(): Array[Int] = hiddenCode

  private def countColors(digits: Array[Int]): Array[Int] = {
    val result = new Array[Int](base)
    digits.foreach((d:Int) => result(d)+=1)
    result
  }

  /** Cache this to be a bit faster when called several times, with different attempts. */
  val hiddenCodeColors = countColors(hiddenCode)

  def divinate(attempt: Array[Int]): OracleResult = {
    var fullMatches = 0
    (hiddenCode zip attempt).foreach {
      case (dh:Int, da:Int) => if(dh == da) fullMatches += 1 }
    val attemptColors = countColors(attempt)
    var colorMatches = 0
    (hiddenCodeColors zip attemptColors).foreach {
      case(a:Int, b:Int) => colorMatches += (if(a < b)  a else b) }
    new OracleResult(fullMatches, colorMatches - fullMatches)
  }
}
