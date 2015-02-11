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
   Simple test for the Oracle that's based on a plain hidden test.
 */
@RunWith(Parameterized.class)
public class HiddenCodeOracleTest {

    @Parameters(name="HiddenCodeOracleTest row {index}")
    public static Collection<Object[]> testData() {
        LinkedList<Object[]> result = new LinkedList<>();
        result.add(new Object[]{new int[]{0,1,2,2,4}, new int[]{0,1,2,2,4}, 5, 5, 0});
        result.add(new Object[]{new int[]{}, new int[]{}, 20, 0, 0});
        result.add(new Object[]{new int[]{0,1,2,1}, new int[]{1,1,3,4}, 6, 1, 1});
        result.add(new Object[]{new int[]{3,2,1,3}, new int[]{1,1,3,4}, 6, 0, 2});
        result.add(new Object[]{new int[]{2,2,5,5}, new int[]{5,5,2,2}, 6, 0, 4});
        result.add(new Object[]{new int[]{1}, new int[]{1}, 6, 1, 0});
        result.add(new Object[]{new int[]{1}, new int[]{5}, 6, 0, 0});
        return result;
    }

    private final int hidden[];
    private final int attempt[];
    private final int base;
    private final int shouldFull;
    private final int shouldPartial;

    public HiddenCodeOracleTest(int hidden[], int attempt[], int base, int shouldFull, int shouldPartial) {
        this.hidden = hidden;
        this.attempt = attempt;
        this.base = base;
        this.shouldFull = shouldFull;
        this.shouldPartial = shouldPartial;
    }

    @Test
    public void test() {
        Oracle oracle = OracleFactory.makeOracleFromHiddenCode(hidden, base);
        assertEquals(hidden.length, oracle.getLength());
        assertEquals(base, oracle.getBase());
        OracleResult result = oracle.divinate(attempt);
        assertEquals(shouldFull, result.getFullMatchCount());
        assertEquals(shouldPartial, result.getPartialMatchCount());
    }
}
