package com.innoq.numbergame.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.innoq.numbergame.baseapi.BadAttemptException;
import com.innoq.numbergame.baseapi.Oracle;
import com.innoq.numbergame.baseapi.OracleResult;

/**
   Simple test for the nice oracle.
 */
public class NiceOracleTest {

    @Test
    public void testNiceWeatherFlight() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeNiceOracle(4,6);
        assertEquals(4, oracle.getLength());
        assertEquals(6, oracle.getBase());
        assertEquals(Oracle.OracleType.NICE, oracle.getType());
        OracleResult result = oracle.divinate(new int[] {0,5,3,3});
        assertEquals(0, result.getPartialMatchCount());
        assertEquals(4, result.getFullMatchCount());
    }

    @Test(expected = BadAttemptException.class)
    public void testDigitToHighInAttempt() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeNiceOracle(4,6);
        assertEquals(4, oracle.getLength());
        assertEquals(6, oracle.getBase());
        assertEquals(Oracle.OracleType.NICE, oracle.getType());
        oracle.divinate(new int[] {0,6,3,3});
    }

    @Test(expected = BadAttemptException.class)
    public void testDigitToLowInAttempt() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeNiceOracle(4,6);
        assertEquals(4, oracle.getLength());
        assertEquals(6, oracle.getBase());
        assertEquals(Oracle.OracleType.NICE, oracle.getType());
        oracle.divinate(new int[] {0,4,3,-1});
    }

    @Test(expected = BadAttemptException.class)
    public void testTooManyDigits() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeNiceOracle(3,6);
        assertEquals(3, oracle.getLength());
        assertEquals(6, oracle.getBase());
        assertEquals(Oracle.OracleType.NICE, oracle.getType());
        oracle.divinate(new int[] {0,4,3,-1});
    }

    @Test(expected = BadAttemptException.class)
    public void testTooFewDigits() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeNiceOracle(5,6);
        assertEquals(5, oracle.getLength());
        assertEquals(6, oracle.getBase());
        assertEquals(Oracle.OracleType.NICE, oracle.getType());
        oracle.divinate(new int[] {0,4,3,-1});
    }

    @Test(expected = BadAttemptException.class)
    public void testContinuePlayingAfterWin() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeNiceOracle(4,6);
        assertEquals(4, oracle.getLength());
        assertEquals(6, oracle.getBase());
        assertEquals(Oracle.OracleType.NICE, oracle.getType());
        OracleResult result = oracle.divinate(new int[] {0,4,3,0});
        assertEquals(0, result.getPartialMatchCount());
        assertEquals(4, result.getFullMatchCount());
        // Can't continue playing after winning.
        oracle.divinate(new int[] {1,2,3,4});
    }

    @Test(expected = BadAttemptException.class)
    public void testIGiveUp() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeNiceOracle(4,6);
        assertEquals(4, oracle.getLength());
        assertEquals(6, oracle.getBase());
        assertEquals(Oracle.OracleType.NICE, oracle.getType());
        int solution[] = oracle.iGiveUp();
        assertNotNull(solution);
        assertEquals(4, solution.length);
        for(int d : solution) {
            assertTrue(0 <= d);
            assertTrue(d < 6);
        }
        // Can't play after giving up.
        oracle.divinate(new int[] {0,4,3,0});
    }
}
