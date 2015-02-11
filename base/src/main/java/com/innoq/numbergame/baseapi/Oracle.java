package com.innoq.numbergame.baseapi;

/**
   Objects implementing {@link Oracle} are conceptually immutable.
   Calling the same get-Method several times on the same object yields
   identical results, calling other methods with the equal arguments yields
   equal results.
 */
public interface Oracle {

    /**
       The valid digits are <code>0 .. getBase()-1</code> inclusively.
     */
    int getBase();

    /**
       Each of your oracle requests, as well as the number you need to determine,
       has this many digits (including leading 0s, if any).

       <p>Value returned will not change during the life time of the particular object
       implementing this interface.</p>
    */
    int getLength();

    /**
       @param digits A vector of length <code>getLength()</code>, each
       individual entry being a valid digit.
     */
    public OracleResult divinate(int[] digits);
}
