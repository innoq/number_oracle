package com.innoq.numbergame.base;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.innoq.numbergame.baseapi.*;
import java.util.Collection;
import java.util.LinkedList;
import static org.junit.Assert.*;

/**
   Simple error handling test for the oracles that are based on a plain hidden test.
 */
public class FixedCodeOracleErrorHandlingTest {

    @Test(expected = RuntimeException.class)
    public void testHiddenCodeContainsDigitTooLow() throws BadAttemptException {
        OracleFactory.makeOracleFromHiddenCode(new int[] {-3}, 5);
    }

    @Test(expected = RuntimeException.class)
    public void testHiddenCodeContainsDigitTooHigh() throws BadAttemptException {
        OracleFactory.makeOracleFromHiddenCode(new int[] {5}, 5);
    }

    @Test(expected = BadAttemptException.class)
    public void testAttemptContainsDigitTooHigh() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeOracleFromHiddenCode(new int[] {4}, 5);
        assertEquals(1, oracle.getLength());
        assertEquals(5, oracle.getBase());
        assertEquals(Oracle.OracleType.FAIR, oracle.getType());
        oracle.divinate(new int[]{5});
    }

    @Test(expected = BadAttemptException.class)
    public void testAttemptContainsDigitTooLow() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeOracleFromHiddenCode(new int[] {0, 0}, 5);
        assertEquals(2, oracle.getLength());
        assertEquals(5, oracle.getBase());
        assertEquals(Oracle.OracleType.FAIR, oracle.getType());
        oracle.divinate(new int[]{-17, 2});
    }

    @Test(expected = BadAttemptException.class)
    public void testAttemptContainsTooManyDigits() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeOracleFromHiddenCode(new int[] {0, 0}, 5);
        assertEquals(2, oracle.getLength());
        assertEquals(5, oracle.getBase());
        assertEquals(Oracle.OracleType.FAIR, oracle.getType());
        oracle.divinate(new int[]{1,2,3});
    }

    @Test(expected = BadAttemptException.class)
    public void testAttemptContainsTooFewDigits() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeOracleFromHiddenCode(new int[] {0, 0}, 5);
        assertEquals(2, oracle.getLength());
        assertEquals(5, oracle.getBase());
        assertEquals(Oracle.OracleType.FAIR, oracle.getType());
        oracle.divinate(new int[]{4});
    }

    @Test(expected = NullPointerException.class)
    public void testAttemptIsNull() throws BadAttemptException {
        Oracle oracle = OracleFactory.makeOracleFromHiddenCode(new int[] {0, 0}, 5);
        assertEquals(2, oracle.getLength());
        assertEquals(5, oracle.getBase());
        assertEquals(Oracle.OracleType.FAIR, oracle.getType());
        oracle.divinate(null);
    }
}
