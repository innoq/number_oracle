package com.innoq.numbergame.base;

import org.junit.Test;

import static org.junit.Assert.*;

import com.innoq.numbergame.baseapi.BadAttemptException;
import com.innoq.numbergame.baseapi.Oracle;

public class RandomOracleTest {
    
    @Test
    public void test() throws BadAttemptException {
        Oracle dut = OracleFactory.makeRandomFairOracle(3, 7);
        assertEquals(Oracle.OracleType.FAIR, dut.getType());
        assertEquals(3, dut.getLength());
        assertEquals(7, dut.getBase());
        
        // Brute force search:
        int[] attempt = new int[3];
        boolean found = false;
        allswell: for(int d0 = 0; d0 < 7; ++d0) {
            attempt[0] = d0;
            for(int d1 = 0; d1 < 7; ++d1) {
                attempt[1] = d1;
                for(int d2= 0; d2 < 7; ++d2) {
                    attempt[2] = d2;
                    if(dut.divinate(attempt).getFullMatchCount() == 2) {
                        found = true;
                        break allswell;
                    }
                }
            }
        }
        assertTrue(found);
    }
}
