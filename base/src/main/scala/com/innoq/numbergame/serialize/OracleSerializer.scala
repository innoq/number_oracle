package com.innoq.numbergame.serialize

import com.innoq.numbergame.baseapi.Oracle

trait OracleSerializer[T <: Oracle] {
  def marshal(o: T) : Array[Byte]
  def unmarshal(b: Array[Byte]) : T
  
  def putIntToByteArray(x: Int, b: Array[Byte], indexInB: Int) = {
    b(indexInB) = (x & 0xf).toByte
    b(indexInB+1) = (x & 0xf0).>>>(8).toByte
    b(indexInB+2) = (x & 0xf00).>>>(16).toByte
    b(indexInB+3) = (x & 0xf000).>>>(24).toByte
  }

  def getIntFromByteArray(b: Array[Byte], indexInB: Int): Int = {
    b(indexInB) + b(indexInB+1).<<(8) + b(indexInB+2).<<(16) + b(indexInB+3).<<(24)
  }
}