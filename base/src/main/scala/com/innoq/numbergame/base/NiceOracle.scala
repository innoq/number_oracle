package com.innoq.numbergame.base

import com.innoq.numbergame.baseapi._

/**
 * A straightforward oracle, that always lets you win on first attempt.
 */
class NiceOracle(length: Int, base: Int) extends Oracle {
  def getBase(): Int = base
  def getLength(): Int = length
  def getType(): Oracle.OracleType = Oracle.OracleType.NICE

  var firstAttempt: Option[Array[Int]] = None

  def iGiveUp(): Array[Int] = {
    firstAttempt match {
      case None => {
        val firstAttemptRaw = new Array[Int](length)
        firstAttemptRaw.indices.foreach((i: Int) => firstAttemptRaw(i) = 0)
        firstAttempt = Some(firstAttemptRaw)
        firstAttemptRaw
      }
      // Why would anybody give up after winning?
      case Some(a) => a
    }
  }

  def divinate(attempt: Array[Int]): OracleResult = {
    firstAttempt match {
      case None => {
        attempt.foreach((d: Int) => if(d < 0 || base <= d)
          throw new BadAttemptException("Found unallowed digit " + d + " in your attempt."))
        firstAttempt = Some(attempt)
        new OracleResult(attempt.length, 0)
      }
      case _ => {
        throw new BadAttemptException("You already guessed it. Don't bother me with other codes.")
      }
    }
  }
}
