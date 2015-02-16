package com.innoq.numbergame.base

import com.innoq.numbergame.baseapi.Oracle
import java.util.Random

object OracleFactory {

  def makeOracleFromHiddenCode(hiddenCode: Array[Int], base: Int) =
    new FixedCodeOracle(hiddenCode, base)

  def makeNiceOracle(length: Int, base: Int) =
    new NiceOracle(length, base)

  def makeRandomFairOracle(length: Int, base: Int) = {
    val r = new Random
    val hiddenCode = new Array[Int](length)
    (0 until length).foreach{hiddenCode(_) = r.nextInt(base)}
    makeOracleFromHiddenCode(hiddenCode, base)
  }
}
