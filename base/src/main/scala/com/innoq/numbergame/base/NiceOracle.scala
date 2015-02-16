package com.innoq.numbergame.base

import com.innoq.numbergame.baseapi._

/**
 * A straightforward oracle, that always lets you win on first attempt.
 */
class NiceOracle(length: Int, base: Int) extends Oracle {
  override def getBase() = base
  override def getLength() = length
  override def getType() = Oracle.OracleType.NICE

  var firstAttempt: Option[Array[Int]] = None

  override def iGiveUp() = {
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

  override def divinate(attempt: Array[Int]) = {
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
