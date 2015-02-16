package com.innoq.numbergame.serialize

import org.junit.Test
import com.innoq.numbergame.base.FixedCodeOracle
import org.junit.Assert

class FixedCodeOracleSerializerTest {

  @Test
  def test = {
    val hiddenCode = Array[Int](4,2,1,5,3)
    val ori = new FixedCodeOracle(hiddenCode, 7)
    val serialize = FixedCodeOracleSerializer.marshal(ori)
    val cpi = FixedCodeOracleSerializer.unmarshal(serialize)
    Assert.assertArrayEquals(ori.iGiveUp(), cpi.iGiveUp())
    Assert.assertEquals(ori.getBase(), cpi.getBase())
  }
}