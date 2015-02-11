package com.innoq.numbergame.base

import com.innoq.numbergame.baseapi.Oracle

object OracleFactory {

  def makeOracleFromHiddenCode(hiddenCode: Array[Int], base: Int): Oracle =
    new FixedCodeOracle(hiddenCode, base)  
}
