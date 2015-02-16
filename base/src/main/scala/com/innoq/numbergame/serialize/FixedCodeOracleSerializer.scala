package com.innoq.numbergame.serialize

import com.innoq.numbergame.base.FixedCodeOracle

object FixedCodeOracleSerializer extends OracleSerializer[FixedCodeOracle] {
	def marshal(o : FixedCodeOracle) : Array[Byte] = {
      val hiddenCode = o.iGiveUp()
      val result = new Array[Byte](8 + hiddenCode.length * 4)
      putIntToByteArray(o.getBase(), result, 0)
      putIntToByteArray(hiddenCode.length, result, 4)
      (0 until hiddenCode.length).foreach(i => {
        putIntToByteArray(hiddenCode(i), result, 8 + i * 4)
      })
      result
	}
	def unmarshal(b : Array[Byte]) : FixedCodeOracle = {
     val base = getIntFromByteArray(b, 0)
     val l = getIntFromByteArray(b, 4)
     val hiddenCode = new Array[Int](l)
     (0 until hiddenCode.length).foreach(i => {
       hiddenCode(i) = getIntFromByteArray(b, 8 + i * 4)
     })
     new FixedCodeOracle(hiddenCode, base)
	}
}